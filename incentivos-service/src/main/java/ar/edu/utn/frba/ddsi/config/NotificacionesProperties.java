package ar.edu.utn.frba.ddsi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service.notificaciones")
@Data
public class NotificacionesProperties {
    private String url;
}