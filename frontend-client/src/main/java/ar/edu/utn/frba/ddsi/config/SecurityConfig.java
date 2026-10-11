package ar.edu.utn.frba.ddsi.config;

import ar.edu.utn.frba.ddsi.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    // Se crea el ProviderManager directamente (sin padre). La versión del ejemplo de alumnos
    // (getSharedObject + @Bean) quedaba como su propio padre: con un login fallido se llamaba
    // a sí misma en un bucle infinito y respondía 500 en lugar de volver al login con error.
    @Bean
    public AuthenticationManager authManager(CustomAuthProvider provider) {
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Públicas (TP: landing, fotos y mapa sin login, legal, registro e inicio de sesión)
                        .requestMatchers("/", "/acceso/**", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                        // Incentivos es solo para la persona humana donante (TP)
                        .requestMatchers("/donante/incentivos").hasRole("DONANTE_HUMANO")
                        .requestMatchers("/donante/**").hasAnyRole("DONANTE_HUMANO", "DONANTE_JURIDICO")
                        .requestMatchers("/entidadBeneficiaria/**").hasRole("ENTIDAD_BENEFICIARIA")

                        // agrego ruta para persona adminsitradora
                        .requestMatchers("/personaAdministradora/**").hasRole("ADMIN")

                        // Lo demás (por ahora logística) solo pide haber iniciado sesión
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/acceso/login")              // GET: muestra el formulario
                        .loginProcessingUrl("/acceso/login")     // POST: lo procesa Spring Security
                        .usernameParameter("email")              // el campo del formulario se llama "email"
                        // Cada rol entra a su pantalla principal
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect(paginaInicial(authentication)))
                        // Credenciales incorrectas vs. servicio de autenticación caído
                        .failureHandler((request, response, exception) ->
                                response.sendRedirect(exception instanceof AuthenticationServiceException
                                        ? "/acceso/login?servicio"
                                        : "/acceso/login?error"))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")                    // POST desde el botón "Cerrar sesión"
                        .logoutSuccessUrl("/acceso/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // Sin sesión → al login
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/acceso/login?unauthorized")
                        )
                        // Con sesión pero sin permiso (por ejemplo, una entidad en /donante) → a su inicio
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect(paginaInicial(
                                        SecurityContextHolder.getContext().getAuthentication()))
                        )
                );

        return http.build();
    }

    private static String paginaInicial(Authentication authentication) {
        if (authentication == null) {
            return "/acceso/landing";
        }
        for (var authority : authentication.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_DONANTE_HUMANO", "ROLE_DONANTE_JURIDICO":
                    return "/donante/misDonaciones";
                case "ROLE_ENTIDAD_BENEFICIARIA":
                    return "/entidadBeneficiaria/recepcionDeDonaciones";
                case "ROLE_ADMIN":
                    return "/personaAdministradora/home";
                default:
                    break;
            }
        }
        // TODO: pantallas de la persona administradora
        return "/acceso/landing";
    }
}
