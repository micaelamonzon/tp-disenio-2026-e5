package ar.edu.utn.frba.ddsi.notificaciones.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitMQConfig {

    @Value("${notificaciones.queue.name}")
    private String queueName;

    // Declara la cola en el broker: si no existe, RabbitMQ la crea automáticamente
    // al levantar el servicio. "durable" = true para que la cola sobreviva
    // si el broker se reinicia.
    @Bean
    public Queue colaNotificaciones() {
        return new Queue(queueName, true);
    }

    // Convierte los mensajes a JSON al publicarlos, así el servicio de
    // notificaciones puede deserializarlos como objeto
    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         JacksonJsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    // Cliente HTTP simple para que el listener pueda resolver el contacto
    // del donante contra donaciones-service (GET /servicioDeDonaciones/humano/{id})
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}