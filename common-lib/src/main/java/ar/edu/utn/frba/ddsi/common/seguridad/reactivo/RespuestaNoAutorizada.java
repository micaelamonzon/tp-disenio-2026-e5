package ar.edu.utn.frba.ddsi.common.seguridad.reactivo;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

// Corta el request con 401 y un JSON {"error": "..."} (mismo formato que los servicios servlet)
final class RespuestaNoAutorizada {

    private RespuestaNoAutorizada() {
    }

    static Mono<Void> enviar(ServerWebExchange exchange, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer cuerpo = response.bufferFactory()
                .wrap(("{\"error\":\"" + mensaje + "\"}").getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(cuerpo));
    }
}
