package ar.edu.utn.frba.ddsi.notificaciones.adapters;

import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// Adapter que adapta la interfaz de Twilio WhatsApp a la interfaz del sistema
@Component
public class TwilioWhatsAppAdapter {

    private final TwilioConfig twilioConfig;

    public TwilioWhatsAppAdapter(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    @Async
    public void enviar(String destinatario, String mensaje) {
        destinatario = destinatario.trim();
        if (!destinatario.startsWith("+")) {
            destinatario = "+" + destinatario;
        }
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
        Message.creator(
                new PhoneNumber("whatsapp:" + destinatario),
                new PhoneNumber("whatsapp:" + twilioConfig.getWhatsappNumber()),
                mensaje
        ).create();
    }
}