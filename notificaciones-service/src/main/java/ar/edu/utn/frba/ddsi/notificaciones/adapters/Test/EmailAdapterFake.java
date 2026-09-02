package ar.edu.utn.frba.ddsi.notificaciones.adapters.Test;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.EmailAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// adapter de prueba: no envia nada real, solo registra que se habria enviado
// se activa con el perfil "test" para probar el flujo sin gastar cuota de SendGrid
@Component
@Profile("test")
public class EmailAdapterFake implements EmailAdapter {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[FAKE EMAIL] a " + destinatario + ": " + mensaje);
    }
}