package ar.edu.utn.frba.ddsi.donaciones.config.UsingConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UsingConfig {
    @Bean
    public RestClient usigClient() {
        return RestClient.builder().build();
    }
}
