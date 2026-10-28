package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.SmsAdapter;
import org.springframework.stereotype.Component;

@Component
public class SMS implements MedioDeNotificacion {

  private final SmsAdapter smsAdapter;

  public SMS(SmsAdapter smsAdapter) {
    this.smsAdapter = smsAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    smsAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}