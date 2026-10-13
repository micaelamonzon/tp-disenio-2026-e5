package ar.edu.utn.frba.ddsi.common.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Lo que todos los servicios necesitan para validar tokens. Cada servicio lo trae con
// @Import(SeguridadComunConfig.class) en su SecurityConfig y define ahí sus propias reglas.
// Necesita en application.properties: jwt.secret (el mismo de autenticacion-service)
// y, si recibe llamadas de otros servicios, api.tokens.<servicio>.
@Configuration
@EnableConfigurationProperties(ApiTokensProperties.class)
public class SeguridadComunConfig {

    @Bean
    public JwtUtil jwtUtil(@Value("${jwt.secret}") String secret) {
        return new JwtUtil(secret);
    }
}
