package ar.edu.utn.frba.ddsi.donaciones.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

// Servicios que pueden llamar a donaciones y el API token de cada uno.
@ConfigurationProperties(prefix = "api")
public record ApiTokensProperties(Map<String, String> tokens) {

    public ApiTokensProperties {
        tokens = tokens != null ? Map.copyOf(tokens) : Map.of();
    }
}
