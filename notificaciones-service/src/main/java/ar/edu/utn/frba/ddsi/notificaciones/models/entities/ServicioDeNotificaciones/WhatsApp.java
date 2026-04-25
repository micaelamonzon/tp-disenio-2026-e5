package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;

public class WhatsApp implements MedioDeNotificacion {
  @Override
  public boolean notificar(String destinatario, Notificacion notificacion) {
    // TO DO: implementar lógica
    return false;
  }
}
