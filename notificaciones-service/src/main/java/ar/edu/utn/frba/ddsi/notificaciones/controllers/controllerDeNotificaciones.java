package ar.edu.utn.frba.ddsi.notificaciones.controllers;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/servicioDeNotificaciones")
public class controllerDeNotificaciones {

    private final Email email;
    private final SMS sms;
    private final WhatsApp whatsApp;
    private final NotificacionRepository notificacionRepository;

    @Autowired
    public controllerDeNotificaciones(Email email, SMS sms, WhatsApp whatsApp,
                                      NotificacionRepository notificacionRepository) {
        this.email = email;
        this.sms = sms;
        this.whatsApp = whatsApp;
        this.notificacionRepository = notificacionRepository;
    }

    @PostMapping("/notificar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String notificar(@RequestParam String destinatario,
                            @RequestParam String mensaje,
                            @RequestParam String medio) {

        destinatario = URLDecoder.decode(destinatario, StandardCharsets.UTF_8);

        System.out.println("Destinatario recibido: [" + destinatario + "]");

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);

        System.out.println("Destinatario recibido: [" + destinatario + "]");
        System.out.println("Mensaje recibido: [" + mensaje + "]");
        System.out.println("Medio recibido: [" + medio + "]");
        System.out.println("Destinatario: " + destinatario.length());

        MedioDeNotificacion medioDeNotificacion = switch (medio.toUpperCase()) {
            case "EMAIL" -> email;
            case "SMS" -> sms;
            case "WHATSAPP" -> whatsApp;
            default -> throw new IllegalArgumentException("Medio no soportado: " + medio);
        };

        notificacion.setMedioDeNotificacion(medioDeNotificacion);
        notificacion.enviar();
        notificacionRepository.save(notificacion); // persiste la notificación con su estado final

        return "Notificacion enviada por " + medio;
    }
}