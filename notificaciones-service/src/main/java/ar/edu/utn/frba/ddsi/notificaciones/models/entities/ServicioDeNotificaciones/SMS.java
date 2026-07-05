package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.TwilioSMSAdapter;
import org.springframework.stereotype.Component;

@Component
public class SMS implements MedioDeNotificacion {

  private final TwilioSMSAdapter twilioSMSAdapter;

  public SMS(TwilioSMSAdapter twilioSMSAdapter) {
    this.twilioSMSAdapter = twilioSMSAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    twilioSMSAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}