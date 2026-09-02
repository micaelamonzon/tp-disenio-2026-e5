package ar.edu.utn.frba.ddsi.notificaciones.adapters.Test;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.WhatsAppAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class WhatsAppAdapterFake implements WhatsAppAdapter {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[FAKE WHATSAPP] a " + destinatario + ": " + mensaje);
    }
}