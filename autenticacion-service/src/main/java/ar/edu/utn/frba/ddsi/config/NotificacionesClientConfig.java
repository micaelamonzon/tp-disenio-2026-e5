package ar.edu.utn.frba.ddsi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NotificacionesClientConfig {
    @Bean
    public RestClient notificacionesRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }
}
