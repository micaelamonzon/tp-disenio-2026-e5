package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhatsApp implements MedioDeNotificacion {

  private final TwilioConfig twilioConfig;

  @Autowired
  public WhatsApp(TwilioConfig twilioConfig) {
    this.twilioConfig = twilioConfig;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    Message.creator(
            new PhoneNumber("whatsapp:" + destinatario),
            new PhoneNumber("whatsapp:" + twilioConfig.getWhatsappNumber()),
            notificacion.getMensaje()
    ).create();
  }
}