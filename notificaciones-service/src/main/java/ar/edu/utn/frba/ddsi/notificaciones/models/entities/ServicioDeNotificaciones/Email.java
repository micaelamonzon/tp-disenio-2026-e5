package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.EmailAdapter;
import org.springframework.stereotype.Component;

@Component
public class Email implements MedioDeNotificacion {

  private final EmailAdapter emailAdapter;

  public Email(EmailAdapter emailAdapter) {
    this.emailAdapter = emailAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    emailAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}