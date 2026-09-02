package ar.edu.utn.frba.ddsi.notificaciones.listeners;

import ar.edu.utn.frba.ddsi.notificaciones.dto.NotificacionMensajeDTO;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

// Consume los mensajes que los servicios de dominio (logística)
// publican en la cola de RabbitMQ. Es la contraparte asincrónica del
// controller: recibe los mismos datos pero desde la cola en vez de HTTP,
// y reutiliza el mismo mecanismo de envío (Notificacion + medio)
@Component
public class NotificacionesListener {

    private final Email email;
    private final SMS sms;
    private final WhatsApp whatsApp;

    // guarda los ids de eventos ya procesados, para descartar reentregas duplicadas de RabbitMQ (idempotencia)
    // manejamos el tamaño acotado con LinkedHashSet + eliminacion del mas viejo para no crecer indefinidamente en memoria
    private final Set<String> eventosProcesados = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final int MAX_EVENTOS_RECORDADOS = 1000;

    public NotificacionesListener(Email email, SMS sms, WhatsApp whatsApp) {
        this.email = email;
        this.sms = sms;
        this.whatsApp = whatsApp;
    }

    @RabbitListener(queues = "${notificaciones.queue.name}")
    public void recibirNotificacion(NotificacionMensajeDTO dto) {
        synchronized (eventosProcesados) {
            if (eventosProcesados.contains(dto.idEvento())) {
                System.out.println("Evento duplicado ignorado: " + dto.idEvento());
                return;
            }
            eventosProcesados.add(dto.idEvento());
            if (eventosProcesados.size() > MAX_EVENTOS_RECORDADOS) {
                eventosProcesados.remove(eventosProcesados.iterator().next());
            }
        }

        System.out.println("Mensaje recibido de la cola: destinatario=[" + dto.destinatario()
                + "] medio=[" + dto.medio() + "]");

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(dto.destinatario());
        notificacion.setMensaje(dto.mensaje());

        MedioDeNotificacion medioDeNotificacion = switch (dto.medio().toUpperCase()) {
            case "EMAIL" -> email;
            case "SMS" -> sms;
            case "WHATSAPP" -> whatsApp;
            default -> throw new IllegalArgumentException("Medio no soportado: " + dto.medio());
        };

        notificacion.setMedioDeNotificacion(medioDeNotificacion);
        notificacion.enviar();
    }
}