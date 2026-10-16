package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.NotificacionMensajeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Publica en la cola de RabbitMQ las notificaciones para donantes.
// Solo manda el id del donante: notificaciones-service resuelve el contacto
// y el medio preferido al consumir el mensaje.
@Service
public class NotificacionesPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public NotificacionesPublisher(RabbitTemplate rabbitTemplate,
                                   @Value("${notificaciones.queue.name}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    // Devuelve true si el mensaje quedó publicado en la cola
    public boolean publicarParaDonante(Long donanteId, String mensaje) {
        try {
            // idEvento único: permite que el listener descarte duplicados (idempotencia)
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, null, donanteId, null, mensaje, null, "HUMANO");
            rabbitTemplate.convertAndSend(queueName, notificacion);
            return true;
        } catch (Exception e) {
            log.error("Error al publicar notificación para donante {}", donanteId, e);
            return false;
        }
    }

    // para la entidad beneficiaria se manda el contacto y el medio del representante
    // y el entidadId para que la notificación aparezca en la pantalla de la entidad
    public boolean publicarParaEntidad(Long entidadId, String contacto, String mensaje, String medio) {
        try {
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, contacto, null, entidadId, mensaje, medio, null);
            rabbitTemplate.convertAndSend(queueName, notificacion);
            return true;
        } catch (Exception e) {
            log.error("Error al publicar notificación para entidad {}", entidadId, e);
            return false;
        }
    }

    // para donantes jurídicos notificaciones-service resuelve el contacto en /donante/juridico/{id} por al tipoDonante
    public boolean publicarParaDonanteJuridico(Long donanteJuridicoId, String mensaje) {
        try {
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, null, donanteJuridicoId, null, mensaje, null, "JURIDICO");
            rabbitTemplate.convertAndSend(queueName, notificacion);
            return true;
        } catch (Exception e) {
            log.error("Error al publicar notificación para donante jurídico {}", donanteJuridicoId, e);
            return false;
        }
    }
}