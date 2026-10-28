package ar.edu.utn.frba.ddsi.notificaciones.config;

import ar.edu.utn.frba.ddsi.common.seguridad.ApiTokensProperties;
import ar.edu.utn.frba.ddsi.common.seguridad.JwtUtil;
import ar.edu.utn.frba.ddsi.common.seguridad.SeguridadComunConfig;
import ar.edu.utn.frba.ddsi.common.seguridad.servlet.ApiTokenFilter;
import ar.edu.utn.frba.ddsi.common.seguridad.servlet.JwtAuthenticationFilter;
import ar.edu.utn.frba.ddsi.common.seguridad.servlet.ReglaDeDuenio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Las clases de seguridad (filtros, JwtUtil, regla de dueño) están en common-lib
// aca solo van las reglas propias de notificaciones
@Configuration
@Import(SeguridadComunConfig.class)
public class SecurityConfig {

    private static final String P = "/servicioDeNotificaciones";

    private static final String HUMANO = "DONANTE_HUMANO";
    private static final String JURIDICO = "DONANTE_JURIDICO";
    private static final String ADMIN = "ADMIN";
    private static final String SERVICIO = ApiTokenFilter.ROL_SERVICIO;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil,
                                                   ApiTokensProperties apiTokens) throws Exception {
        http
                // Es una API, quién llama se sabe por el token de cada request.
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // preflight CORS

                        // ---------- Entre servicios (API token, ROLE_SERVICIO) ----------
                        // donaciones (asignaciones) y autenticación (recuperar contraseña) piden enviar un aviso
                        .requestMatchers(HttpMethod.POST, P + "/notificar").hasAnyRole(SERVICIO, ADMIN)

                        // ---------- Cada uno ve solo sus notificaciones ----------
                        .requestMatchers(HttpMethod.GET, P + "/donantes/{idDonante}/notificaciones")
                                .access(ReglaDeDuenio.donante(HUMANO))
                        .requestMatchers(HttpMethod.GET, P + "/juridicos/{idDonante}/notificaciones")
                                .access(ReglaDeDuenio.donante(JURIDICO))
                        .requestMatchers(HttpMethod.GET, P + "/entidades/{idEntidad}/notificaciones")
                                .access(ReglaDeDuenio.entidad())

                        // Cualquier endpoint nuevo queda protegido hasta que se decida quién lo usa
                        .anyRequest().authenticated()
                )
                // Sin token (o vencido) -> 401, así el front sabe que tiene que refrescarlo.
                // Con token pero sin el rol -> 403.
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // Servicios: X-Api-Token. Usuarios: Authorization: Bearer <JWT>
                .addFilterBefore(new ApiTokenFilter(apiTokens), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
