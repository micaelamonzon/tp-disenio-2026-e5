package ar.edu.utn.frba.ddsi.config;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LogisticaProperties {
    private String urlPlanificador;
    private String clientSecret;
}
