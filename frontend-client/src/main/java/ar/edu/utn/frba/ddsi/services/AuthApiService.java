package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.autenticacion.AuthResponseDTO;
import ar.edu.utn.frba.ddsi.dtos.autenticacion.RecuperarPasswordRequestDTO;
import ar.edu.utn.frba.ddsi.dtos.autenticacion.TokenRespuestaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class AuthApiService {

    private static final Logger log = LoggerFactory.getLogger(AuthApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String authServiceUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthApiService(@Value("${auth.service.url}") String authServiceUrl) {
        this.authServiceUrl = authServiceUrl;
    }

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
    /**
     * Extrae el claim 'rol' del JWT devuelto por el servidor de autenticación
     */
    public String extraerRolDelJwt(String jwtToken) {
        try {
            if (jwtToken == null || jwtToken.isBlank()) return "DONANTE_HUMANO";

            String[] partes = jwtToken.split("\\.");
            if (partes.length < 2) return "DONANTE_HUMANO";

            String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));
            JsonNode node = objectMapper.readTree(payloadJson);

            return node.has("rol") ? node.get("rol").asText() : "DONANTE_HUMANO";
        } catch (Exception e) {
            log.warn("No se pudo extraer el claim 'rol' del JWT: {}", e.getMessage());
            return "DONANTE_HUMANO";
        }
    }

    // Se valida antes de crear el donante/entidad: si el email ya tiene cuenta no se crea nada
    public void validarEmailDisponible(String email) {
        Map respuesta;
        try {
            respuesta = webClient.get()
                    .uri(authServiceUrl + "/usuarios/disponible?email={email}", email)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException(motivoDelError(e), e); // 400: email con formato inválido
        } catch (Exception e) {
            log.error("Error consultando email disponible en servicio de autenticación: {}", e.getMessage());
            throw new RuntimeException("El servicio de autenticación no está disponible", e);
        }
        if (respuesta == null || !Boolean.TRUE.equals(respuesta.get("disponible"))) {
            throw new RuntimeException("Ya existe una cuenta con ese email");
        }
    }

    public void crearUsuario(String email, String password, String rol, Long idEnDominio, String nombre) {
        if (idEnDominio == null) {
            throw new IllegalArgumentException("Falta el id del donante o la entidad para crear el usuario");
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);
            body.put("rol", rol); // "DONANTE_HUMANO", "DONANTE_JURIDICO" o "ENTIDAD_BENEFICIARIA"
            body.put("idEnDominio", idEnDominio);
            body.put("nombre", nombre);

            webClient.post()
                    .uri(authServiceUrl + "/usuarios")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Usuario {} registrado exitosamente en servicio de autenticación", email);

        } catch (WebClientResponseException e) {
            // 409 = email ya registrado, 400 = datos inválidos: auth explica el motivo en "error"
            log.error("Autenticación rechazó el alta de {}: {} {}", email, e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new RuntimeException("Ya existe una cuenta con ese email", e);
            }
            throw new RuntimeException(motivoDelError(e), e);
        } catch (Exception e) {
            log.error("Error creando usuario en servicio de autenticación: {}", e.getMessage());
            throw new RuntimeException("El servicio de autenticación no está disponible", e);
        }
    }

    private String motivoDelError(WebClientResponseException e) {
        try {
            JsonNode error = objectMapper.readTree(e.getResponseBodyAsString()).get("error");
            if (error != null) {
                return error.asText();
            }
        } catch (Exception ignorada) {
            // cuerpo vacío o no JSON: se usa el mensaje genérico
        }
        return "No se pudo crear la cuenta de usuario";
    }
    public void solicitarRecuperacion(String email) {
        try {
            webClient.post()
                    .uri(authServiceUrl + "/auth/solicitar-recuperacion")
                    .bodyValue(new RecuperarPasswordRequestDTO(email))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error al solicitar recuperación: {}", e.getMessage());
            throw new RuntimeException("No se pudo procesar la solicitud", e);
        }
    }

    public void cambiarPasswordConToken(String token, String nuevaPassword) {
        try {
            var body = java.util.Map.of("token", token, "nuevaPassword", nuevaPassword);
            webClient.post()
                    .uri(authServiceUrl + "/auth/cambiar-con-token")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error al actualizar contraseña con token: {}", e.getMessage());
            throw new RuntimeException("El enlace de recuperación es inválido o ha expirado", e);
        }
    }
}
