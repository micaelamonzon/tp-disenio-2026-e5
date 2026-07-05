package ar.edu.utn.frba.ddsi.notificaciones.adapters;

import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// Adapter que adapta la interfaz de SendGrid a la interfaz del sistema
@Component
public class SendGridEmailAdapter {

    private final TwilioConfig twilioConfig;

    public SendGridEmailAdapter(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    @Async
    public void enviar(String destinatario, String mensaje) {
        com.sendgrid.helpers.mail.objects.Email from =
                new com.sendgrid.helpers.mail.objects.Email(twilioConfig.getSendgridFromEmail());
        com.sendgrid.helpers.mail.objects.Email to =
                new com.sendgrid.helpers.mail.objects.Email(destinatario);
        Content content = new Content("text/plain", mensaje);
        Mail mail = new Mail(from, "Notificación DonaTrack", to, content);

        SendGrid sg = new SendGrid(twilioConfig.getSendgridApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email", e);
        }
    }
}