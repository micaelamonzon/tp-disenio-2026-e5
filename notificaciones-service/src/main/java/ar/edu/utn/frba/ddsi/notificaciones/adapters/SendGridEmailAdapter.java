package ar.edu.utn.frba.ddsi.notificaciones.adapters;

import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;

// Adapter que adapta la interfaz de SendGrid a la interfaz del sistema
@Component
@Profile("!test")
@ConditionalOnProperty(name = "notificaciones.email.proveedor", havingValue = "sendgrid", matchIfMissing = true)
public class SendGridEmailAdapter implements EmailAdapter {

    private final TwilioConfig twilioConfig;

    public SendGridEmailAdapter(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    @Override
    public void enviar(String destinatario, String mensaje) {
        com.sendgrid.helpers.mail.objects.Email from =
                new com.sendgrid.helpers.mail.objects.Email(twilioConfig.getSendgridFromEmail());
        com.sendgrid.helpers.mail.objects.Email to =
                new com.sendgrid.helpers.mail.objects.Email(destinatario);
        Content content = new Content("text/plain", mensaje);
        Mail mail = new Mail(from, "Notificación DonaTrack", to, content);

        SendGrid sg = new SendGrid(twilioConfig.getSendgridApiKey());
        Request request = new Request();
        Response response;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión con SendGrid", e);
        }

        // Si no es 2xx, es una excepción para que la notificación quede FALLIDA
        int codigo = response.getStatusCode();
        if (codigo < 200 || codigo >= 300) {
            throw new RuntimeException("SendGrid rechazó el email (código " + codigo + "): "
                    + response.getBody());
        }
    }
}