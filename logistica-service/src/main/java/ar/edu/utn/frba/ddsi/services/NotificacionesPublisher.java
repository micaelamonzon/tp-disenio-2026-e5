package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.NotificacionMensajeDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Publica notificaciones en la cola de RabbitMQ en lugar de llamar
// directamente al servicio de notificaciones por HTTP.
// Así la integración es asincrónica: si notificaciones está caído o
// saturado, los mensajes quedan encolados y logística no se ve afectada.
@Component
public class NotificacionesPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public NotificacionesPublisher(RabbitTemplate rabbitTemplate,
                                   @Value("${notificaciones.queue.name}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void publicar(String destinatario, String mensaje, String medio) {
        try {
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(destinatario, mensaje, medio);
            rabbitTemplate.convertAndSend(queueName, notificacion);
        } catch (Exception e) {
            // Si falla la publicación no queremos que se caiga la operación
            // de negocio (ej: confirmar una entrega)
            System.out.println("Error al publicar notificación en la cola: " + e.getMessage());
        }
    }
}