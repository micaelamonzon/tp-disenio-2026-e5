package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.WhatsAppAdapter;
import org.springframework.stereotype.Component;

@Component
public class WhatsApp implements MedioDeNotificacion {

  private final WhatsAppAdapter whatsAppAdapter;

  public WhatsApp(WhatsAppAdapter whatsAppAdapter) {
    this.whatsAppAdapter = whatsAppAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    whatsAppAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}