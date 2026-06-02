package ar.edu.utn.frba.ddsi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "n8n")
@Data
public class N8NProperties {
    private String baseURL;
}
