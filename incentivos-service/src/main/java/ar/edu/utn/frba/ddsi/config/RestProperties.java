package ar.edu.utn.frba.ddsi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service.donaciones")
@Data
public class RestProperties {
    private String url;
}
