package ar.edu.utn.frba.ddsi;

import ar.edu.utn.frba.ddsi.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.config.RestProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RestProperties.class, NotificacionesProperties.class})
public class LogisticaServiceAppliacion {
    public static void main ( String[] args )
    {
        SpringApplication.run(LogisticaServiceAppliacion.class, args);
    }
}
