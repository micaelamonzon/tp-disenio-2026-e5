package ar.edu.utn.frba.ddsi.donaciones.config.webHook;

import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebHookConfig {
    @Bean
    public RestClient incentivosClient(@Value("${incentivos.service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
