package ar.edu.utn.frba.ddsi.notificaciones.controllers;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicioDeNotificaciones")
public class controllerDeNotificaciones {

    private final Email email;
    private final SMS sms;
    private final WhatsApp whatsApp;

    @Autowired
    public controllerDeNotificaciones(Email email, SMS sms, WhatsApp whatsApp) {
        this.email = email;
        this.sms = sms;
        this.whatsApp = whatsApp;
    }

    @PostMapping("/notificar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String notificar(@RequestParam String destinatario,
                            @RequestParam String mensaje,
                            @RequestParam String medio) {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);

        MedioDeNotificacion medioDeNotificacion = switch (medio.toUpperCase()) {
            case "EMAIL" -> email;
            case "SMS" -> sms;
            case "WHATSAPP" -> whatsApp;
            default -> throw new IllegalArgumentException("Medio no soportado: " + medio);
        };

        notificacion.setMedioDeNotificacion(medioDeNotificacion);
        notificacion.enviar();

        return "Notificacion enviada por " + medio;
    }
}