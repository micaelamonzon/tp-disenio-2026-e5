package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Email implements MedioDeNotificacion {

  private final TwilioConfig twilioConfig;

  @Autowired
  public Email(TwilioConfig twilioConfig) {
    this.twilioConfig = twilioConfig;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    com.sendgrid.helpers.mail.objects.Email from =
            new com.sendgrid.helpers.mail.objects.Email(twilioConfig.getSendgridFromEmail());
    com.sendgrid.helpers.mail.objects.Email to =
            new com.sendgrid.helpers.mail.objects.Email(destinatario);
    Content content = new Content("text/plain", notificacion.getMensaje());
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