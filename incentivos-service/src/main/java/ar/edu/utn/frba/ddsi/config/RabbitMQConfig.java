package ar.edu.utn.frba.ddsi.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configura RabbitMQ en incentivos para publicar las notificaciones como JSON,
// igual que logistica. La cola la declara notificaciones-service (el que consume).
@Configuration
public class RabbitMQConfig {

    // Spring Boot usa este converter automáticamente en el RabbitTemplate,
    // así los mensajes salen como JSON y el listener los puede leer
    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}