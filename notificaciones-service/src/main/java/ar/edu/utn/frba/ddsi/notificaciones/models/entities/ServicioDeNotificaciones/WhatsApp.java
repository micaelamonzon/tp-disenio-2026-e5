package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.TwilioWhatsAppAdapter;
import org.springframework.stereotype.Component;

@Component
public class WhatsApp implements MedioDeNotificacion {

  private final TwilioWhatsAppAdapter twilioWhatsAppAdapter;

  public WhatsApp(TwilioWhatsAppAdapter twilioWhatsAppAdapter) {
    this.twilioWhatsAppAdapter = twilioWhatsAppAdapter;
  }

  @Override
  public void notificar(String destinatario, Notificacion notificacion) {
    twilioWhatsAppAdapter.enviar(destinatario, notificacion.getMensaje());
  }
}