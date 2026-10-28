package ar.edu.utn.frba.ddsi.services.internal;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.utn.frba.ddsi.dtos.autenticacion.AuthResponseDTO;
import ar.edu.utn.frba.ddsi.dtos.autenticacion.RefreshTokenDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

//Llamadas HTTP del front a los servicios con el access token del usuario logueado.

@Service
public class WebApiCallerService {

    private static final Logger log = LoggerFactory.getLogger(WebApiCallerService.class);
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    private final WebClient webClient = WebClient.builder().build();
    private final String authServiceUrl;

    public WebApiCallerService(@Value("${auth.service.url}") String authServiceUrl) {
        this.authServiceUrl = authServiceUrl;
    }

    public <T> T get(String url, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.get()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .retrieve()
                .bodyToMono(responseType)
                .block());
    }

    public <T> List<T> getList(String url, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.get()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .retrieve()
                .bodyToFlux(responseType)
                .collectList()
                .block());
    }

    public <T> List<T> postList(String url, Object body, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.post()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(responseType)
                .collectList()
                .block());
    }

    public <T> T post(String url, Object body, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.post()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block());
    }

    public <T> T put(String url, Object body, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.put()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block());
    }

    public <T> T patch(String url, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.patch()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .retrieve()
                .bodyToMono(responseType)
                .block());
    }

    // PATCH con cuerpo (mismo orden de parámetros que post y put)
    public <T> T patch(String url, Object body, Class<T> responseType, Object... uriVariables) {
        return ejecutar(token -> webClient.patch()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block());
    }

    public void delete(String url, Object... uriVariables) {
        ejecutar(token -> webClient.delete()
                .uri(url, uriVariables)
                .headers(h -> agregarToken(h, token))
                .retrieve()
                .toBodilessEntity()
                .block());
    }

    private <T> T ejecutar(ApiCall<T> apiCall) {
        String accessToken = leerDeSesion(ACCESS_TOKEN);
        try {
            return apiCall.execute(accessToken);
        } catch (WebClientResponseException e) {
            // Solo 401 significa "token vencido o inválido". Un 403 es "no tenés permiso":
            // refrescar no lo arregla, así que se relanza como cualquier otro error
            if (e.getStatusCode() != HttpStatus.UNAUTHORIZED || accessToken == null) {
                throw e;
            }
            return apiCall.execute(refrescarTokens());
        }
    }

    // Pide un access token nuevo con el refresh token y actualiza la sesión
    private String refrescarTokens() {
        String refreshToken = leerDeSesion(REFRESH_TOKEN);
        if (refreshToken == null) {
            throw new SesionExpiradaException();
        }
        AuthResponseDTO tokens;
        try {
            tokens = webClient.post()
                    .uri(authServiceUrl + "/auth/refresh")
                    .bodyValue(RefreshTokenDTO.builder().refreshToken(refreshToken).build())
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            // Autenticación respondió pero no acepta el refresh (vencido, usuario dado de baja...)
            log.info("Autenticación rechazó el refresh token: {}", e.getStatusCode());
            throw new SesionExpiradaException();
        }
        // Si autenticación no responde, el error de conexión sigue de largo:
        // la pantalla muestra "servicio no disponible" y la sesión se mantiene
        if (tokens == null || tokens.getAccessToken() == null) {
            throw new SesionExpiradaException();
        }
        HttpSession session = sesionActualSiExiste();
        if (session != null) {
            session.setAttribute(ACCESS_TOKEN, tokens.getAccessToken());
            if (tokens.getRefreshToken() != null) {
                session.setAttribute(REFRESH_TOKEN, tokens.getRefreshToken());
            }
        }
        log.info("Access token renovado con el refresh token");
        return tokens.getAccessToken();
    }

    private void agregarToken(HttpHeaders headers, String token) {
        if (token != null) {
            headers.setBearerAuth(token);
        }
    }

    private String leerDeSesion(String atributo) {
        HttpSession session = sesionActualSiExiste();
        return session != null ? (String) session.getAttribute(atributo) : null;
    }

    private HttpSession sesionActualSiExiste() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null; // fuera de un request web
        }
        return attributes.getRequest().getSession(false);
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T execute(String accessToken);
    }
}
