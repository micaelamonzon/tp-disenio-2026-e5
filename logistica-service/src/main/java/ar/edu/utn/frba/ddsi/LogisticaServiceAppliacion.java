package ar.edu.utn.frba.ddsi;

import ar.edu.utn.frba.ddsi.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.config.RestProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({RestProperties.class, NotificacionesProperties.class})
@EnableScheduling
public class LogisticaServiceAppliacion {
    public static void main ( String[] args )
    {
        SpringApplication.run(LogisticaServiceAppliacion.class, args);
    }
}
