package ar.edu.utn.frba.ddsi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "notificaciones")
public class NotificacionesProperties {
    private String url;
    private String adminContacto;
}