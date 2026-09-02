package ar.edu.utn.frba.ddsi.notificaciones.converters;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

// Convierte el medio de notificacion (objeto en memoria) a un String para guardarlo en la base
// Como Email/SMS/WhatsApp son beans de Spring (ya tienen su adapter inyectado)
// el converter reutiliza esas mismas instancias en vez de crear objetos nuevos
@Converter(autoApply = true)
@Component
public class MedioDeNotificacionAttributeConverter
        implements AttributeConverter<MedioDeNotificacion, String> {

    private final Email email;
    private final SMS sms;
    private final WhatsApp whatsApp;

    public MedioDeNotificacionAttributeConverter(Email email, SMS sms, WhatsApp whatsApp) {
        this.email = email;
        this.sms = sms;
        this.whatsApp = whatsApp;
    }

    @Override
    public String convertToDatabaseColumn(MedioDeNotificacion medio) {
        if (medio == null) return null;
        if (medio instanceof Email) return "EMAIL";
        if (medio instanceof SMS) return "SMS";
        if (medio instanceof WhatsApp) return "WHATSAPP";
        return null;
    }

    @Override
    public MedioDeNotificacion convertToEntityAttribute(String s) {
        if (s == null) return null;
        return switch (s) {
            case "EMAIL" -> email;
            case "SMS" -> sms;
            case "WHATSAPP" -> whatsApp;
            default -> null;
        };
    }
}