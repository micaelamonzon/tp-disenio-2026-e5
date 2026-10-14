package ar.edu.utn.frba.ddsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Hashea y compara contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Es una API: no hay formularios ni cookies de sesión
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Puerta de entrada: login y refresh son públicos
                        .requestMatchers("/api/auth", "/api/auth/refresh", "/api/auth/solicitar-recuperacion", "/api/auth/cambiar-con-token").permitAll()
                        // TODO: proteger con rol SERVICIO/ADMIN. Mientras tanto queda abierto
                        // y el alta rechaza el rol ADMIN.
                        .requestMatchers("/api/usuarios", "/api/usuarios/disponible").permitAll()
                        // Spring arma las respuestas de error en /error: si se bloquea,
                        // cualquier 400/404/500 se vería como 403
                        .requestMatchers("/error").permitAll()
                        .anyRequest().denyAll()
                );
        return http.build();
    }
}
