package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.NotificacionMensajeDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.UUID;

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

    // Publica una notificación con el contacto ya resuelto (uso actual:
    // entidad beneficiaria y administradores)
    public void publicar(String destinatario, String mensaje, String medio) {
        try {
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, destinatario, null, null, mensaje, medio, null);
            rabbitTemplate.convertAndSend(queueName, notificacion);
        } catch (Exception e) {
            System.out.println("Error al publicar notificación en la cola: " + e.getMessage());
        }
    }

    // Publica una notificación para el donante, sin resolver el contacto:
    // notificaciones-service lo resuelve al consumir el mensaje
    public void publicarParaDonante(Long donanteId, String mensaje, String medio) {
        try {
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, null, donanteId, null, mensaje, medio, null);
            rabbitTemplate.convertAndSend(queueName, notificacion);
        } catch (Exception e) {
            System.out.println("Error al publicar notificación en la cola: " + e.getMessage());
        }
    }


    // Publica una notificación para una entidad beneficiaria: el contacto ya viene
    // resuelto y además se manda su id, para que aparezca en su pantalla de notificaciones
    public void publicarParaEntidad(Long entidadId, String destinatario, String mensaje, String medio) {
        try {
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, destinatario, null, entidadId, mensaje, medio, null);
            rabbitTemplate.convertAndSend(queueName, notificacion);
        } catch (Exception e) {
            System.out.println("Error al publicar notificación en la cola: " + e.getMessage());
        }
    }

    // se indica si el donante es HUMANO o JURIDICo para que notificaciones-service busque el contacto en la tabla correcta
    public void publicarParaDonante(Long donanteId, String mensaje, String medio, String tipoDonante) {
        try {
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, null, donanteId, null, mensaje, medio, tipoDonante);
            rabbitTemplate.convertAndSend(queueName, notificacion);
        } catch (Exception e) {
            System.out.println("Error al publicar notificación en la cola: " + e.getMessage());
        }
    }
}