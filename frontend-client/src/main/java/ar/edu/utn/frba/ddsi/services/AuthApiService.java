package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.AuthResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;


@Service
public class AuthApiService {

    private static final Logger log = LoggerFactory.getLogger(AuthApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String authServiceUrl;

    public AuthApiService(@Value("${auth.service.url}") String authServiceUrl) {
        this.authServiceUrl = authServiceUrl;
    }

    /**
     * POST /api/auth. Devuelve null si las credenciales son incorrectas
     * (autenticacion-service responde 401 o 400 si falta el email o la contraseña).
     * Cualquier otro error (servicio caído, 500) se lanza como excepción.
     */
    public AuthResponseDTO login(String email, String password) {
        try {
            return webClient.post()
                    .uri(authServiceUrl + "/auth")
                    .bodyValue(Map.of("email", email, "password", password))
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return null;
            }
            log.error("Error del servicio de autenticación: {}", e.getMessage());
            throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }
}
