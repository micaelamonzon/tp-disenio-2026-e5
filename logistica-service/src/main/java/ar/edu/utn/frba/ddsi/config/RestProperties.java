package ar.edu.utn.frba.ddsi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rest")
public class RestProperties {
    private String url;
}