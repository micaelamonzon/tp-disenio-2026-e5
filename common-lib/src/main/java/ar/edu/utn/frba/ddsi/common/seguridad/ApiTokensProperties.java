package ar.edu.utn.frba.ddsi.common.seguridad;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@ConfigurationProperties(prefix = "api")
public record ApiTokensProperties(Map<String, String> tokens) {

    public ApiTokensProperties {
        tokens = tokens != null ? Map.copyOf(tokens) : Map.of();
    }

    // Devuelve el nombre del servicio dueño del token, o null si no es de ninguno
    public String buscarServicio(String token) {
        byte[] recibido = token.getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entrada : tokens.entrySet()) {
            // Comparación en tiempo constante: no revela cuántos caracteres coinciden
            if (MessageDigest.isEqual(recibido, entrada.getValue().getBytes(StandardCharsets.UTF_8))) {
                return entrada.getKey();
            }
        }
        return null;
    }
}
