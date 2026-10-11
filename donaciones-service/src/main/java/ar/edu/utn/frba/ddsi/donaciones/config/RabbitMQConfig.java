package ar.edu.utn.frba.ddsi.donaciones.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configura RabbitMQ en donaciones para publicar las notificaciones como JSON.
// La cola la declara notificaciones-service (el que consume).
@Configuration
public class RabbitMQConfig {

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}