package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.SendGridEmailAdapter;
import org.springframework.stereotype.Component;

@Component
public class Email implements MedioDeNotificacion {

  private final SendGridEmailAdapter sendGridAdapter;

  public Email(SendGridEmailAdapter sendGridAdapter) {
    this.sendGridAdapter = sendGridAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    sendGridAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}