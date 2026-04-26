package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;

public class Email implements MedioDeNotificacion {
  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    // TODO: implementar lógica
  }
}
