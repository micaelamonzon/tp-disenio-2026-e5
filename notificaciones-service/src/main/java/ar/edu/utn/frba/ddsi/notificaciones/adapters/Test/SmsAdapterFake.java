package ar.edu.utn.frba.ddsi.notificaciones.adapters.Test;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.SmsAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class SmsAdapterFake implements SmsAdapter {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[FAKE SMS] a " + destinatario + ": " + mensaje);
    }
}