package ar.edu.utn.frba.ddsi.notificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

public class NotificacionTest {
    @Test
    public void notificacionEnviada() {
        String destinatario = "ana@mail.com";
        String mensaje = "Tu donación fue asignada a una entidad beneficiaria.";

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);

        MedioDeNotificacion medioMock = mock(MedioDeNotificacion.class);
        notificacion.setMedioDeNotificacion(medioMock);

        Assertions.assertDoesNotThrow(notificacion::enviar);
    }
}
