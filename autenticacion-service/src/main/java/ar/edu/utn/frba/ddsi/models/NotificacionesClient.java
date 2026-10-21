package ar.edu.utn.frba.ddsi.models;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ar.edu.utn.frba.ddsi.dtos.NotificacionMensajeDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Component
public class NotificacionesClient {
    //private static RestClient client = null;
    private static RabbitTemplate rabbitTemplate;
    private static String queueName;

    /*public NotificacionesClient(RestClient notificacionesRestClient) {
        this.client = notificacionesRestClient;
    }

    public static void enviarEmail(String destinatario, String mensaje) {
        client.post()
                .uri(b -> b.path("/servicioDeNotificaciones/notificar")
                        .queryParam("destinatario", "{destinatario}")
                        .queryParam("mensaje", "{mensaje}")
                        .queryParam("medio", "EMAIL")
                        .build(destinatario, mensaje))
                .retrieve()
                .toBodilessEntity();
    } */

    public NotificacionesClient(RabbitTemplate rabbitTemplate,
                                @Value("${notificaciones.queue.name}") String queueName) {
        NotificacionesClient.rabbitTemplate = rabbitTemplate;
        NotificacionesClient.queueName = queueName;
    }

    public static void enviarEmail(String destinatario, String mensaje) {
        // idEvento unico, permite que el listener descarte duplicados
        NotificacionMensajeDTO notificacion = new NotificacionMensajeDTO(
                UUID.randomUUID().toString(), destinatario, null, null, mensaje, "EMAIL", null);
        rabbitTemplate.convertAndSend(queueName, notificacion);
    }
}
