package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMS implements MedioDeNotificacion {

  private final TwilioConfig twilioConfig;

  @Autowired
  public SMS(TwilioConfig twilioConfig) {
    this.twilioConfig = twilioConfig;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    Message.creator(
            new PhoneNumber(destinatario),
            new PhoneNumber(twilioConfig.getFromNumber()),
            notificacion.getMensaje()
    ).create();
  }
}