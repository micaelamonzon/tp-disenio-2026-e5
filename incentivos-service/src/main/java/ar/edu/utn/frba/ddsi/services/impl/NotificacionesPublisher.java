package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.dto.NotificacionMensajeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Publica en la cola de RabbitMQ las notificaciones para donantes.
// Incentivos solo manda el id del donante: notificaciones-service resuelve
// el contacto y el medio preferido al consumir el mensaje.
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

    public void publicarParaDonante(Long donanteId, String mensaje, String tipoDonante) {
        try {
            // idEvento único: permite que el listener descarte mensajes duplicados (idempotencia)
            String idEvento = UUID.randomUUID().toString();
            NotificacionMensajeDTO notificacion =
                    new NotificacionMensajeDTO(idEvento, null, donanteId, null, mensaje, null, tipoDonante);
            rabbitTemplate.convertAndSend(queueName, notificacion);
        } catch (Exception e) {
            // Si falla la publicación no cortamos el scheduler: el progreso de la
            // misión ya se calculó; solo se pierde este aviso puntual.
            log.error("Error al publicar notificación para donante {}", donanteId, e);
        }
    }
}