package ar.edu.utn.frba.ddsi.notificaciones.listeners;

import ar.edu.utn.frba.ddsi.notificaciones.dto.DonanteJuridicoDTO;
import ar.edu.utn.frba.ddsi.notificaciones.dto.NotificacionMensajeDTO;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Donante;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.NotificacionEstadoHistorial;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.NotificacionEstadoHistorialRepository;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.NotificacionRepository;
import ar.edu.utn.frba.ddsi.notificaciones.services.DonanteService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

// consume los mensajes que los servicios de dominio (logistica)
// publican en la cola de RabbitMQ. Es la contraparte asincronica del
// controller: recibe los mismos datos pero desde la cola en vez de HTTP,
// y reutiliza el mismo mecanismo de envio (notificacion + medio)
@Component
public class NotificacionesListener {

    private final Email email;
    private final SMS sms;
    private final WhatsApp whatsApp;
    private final NotificacionRepository notificacionRepository;
    private final NotificacionEstadoHistorialRepository historialRepository;
    private final DonanteService donanteService;

    // guarda los ids de eventos ya procesados, para descartar reentregas duplicadas de RabbitMQ (idempotencia)
    // manejamos el tamaño acotado con LinkedHashSet + eliminacion del mas viejo para no crecer indefinidamente en memoria
    private final Set<String> eventosProcesados = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final int MAX_EVENTOS_RECORDADOS = 1000;

    public NotificacionesListener(Email email, SMS sms, WhatsApp whatsApp,
                                  NotificacionRepository notificacionRepository,
                                  NotificacionEstadoHistorialRepository historialRepository,
                                  DonanteService donanteService) {
        this.email = email;
        this.sms = sms;
        this.whatsApp = whatsApp;
        this.notificacionRepository = notificacionRepository;
        this.historialRepository = historialRepository;
        this.donanteService = donanteService;
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

        // si el mensaje trae donanteId, el contacto se resuelve contra donaciones-service
        // tipoDonante dice en qué tabla buscarlo, pero si no esta es HUMANO
        // si trae destinatario (entidad o admin) ya viene resuelto
        boolean esJuridico = "JURIDICO".equalsIgnoreCase(dto.tipoDonante());
        String destinatarioFinal;
        String medioDelDonante = null;
        Donante donante = null;
        Long donanteJuridicoId = null;
        try {
            if (dto.donanteId() != null && esJuridico) {
                DonanteJuridicoDTO.Medio contacto = donanteService.resolverContactoJuridico(dto.donanteId());
                destinatarioFinal = contacto.datoDeContacto();
                medioDelDonante = contacto.tipoDeNotificacion();
                donanteJuridicoId = dto.donanteId();
            } else if (dto.donanteId() != null) {
                donante = donanteService.resolverYActualizarDonante(dto.donanteId());
                destinatarioFinal = donante.getContacto();
                medioDelDonante = donante.getTipoDeNotificacion();
            } else {
                destinatarioFinal = dto.destinatario();
            }
        } catch (RestClientException | IllegalStateException e) {
            System.err.println("No se pudo resolver el contacto para el evento "
                    + dto.idEvento() + ": " + e.getMessage());
            return;
        }

        System.out.println("Mensaje recibido de la cola: destinatario=[" + destinatarioFinal
                + "] medio=[" + dto.medio() + "]");

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatarioFinal);
        notificacion.setDonante(donante); // null si no es para un donante humano
        notificacion.setDonanteJuridicoId(donanteJuridicoId); // null si no es para un donante jurídico
        notificacion.setEntidadBeneficiariaId(dto.entidadId()); // null si no es para una entidad
        notificacion.setMensaje(dto.mensaje());

        // Si el mensaje no trae medio usamos el medio preferido del donante, que viene de donaciones-service
        String medio = dto.medio() != null ? dto.medio() : medioDelDonante;

        MedioDeNotificacion medioDeNotificacion = switch (medio == null ? "" : medio.toUpperCase()) {
            case "EMAIL" -> email;
            case "SMS" -> sms;
            case "WHATSAPP" -> whatsApp;
            default -> null;
        };

        if (medioDeNotificacion == null) {
            System.err.println("Medio no soportado o ausente para el evento "
                    + dto.idEvento() + ": " + medio);
            return;
        }

        notificacion.setMedioDeNotificacion(medioDeNotificacion);
        notificacion.enviar();
        notificacionRepository.save(notificacion); // persiste la notificación consumida de la cola
        registrarCambioDeEstado(notificacion); // deja constancia del estado resultante en el historial
    }

    // deja una fila en el historial con el estado en que quedó la
    // notificación después de intentar enviarla (ENVIADA o FALLIDA)
    private void registrarCambioDeEstado(Notificacion notificacion) {
        NotificacionEstadoHistorial registro = new NotificacionEstadoHistorial();
        registro.setNotificacion(notificacion);
        registro.setEstado(notificacion.getEstado());
        registro.setFechaCambio(LocalDateTime.now());
        historialRepository.save(registro);
    }
}