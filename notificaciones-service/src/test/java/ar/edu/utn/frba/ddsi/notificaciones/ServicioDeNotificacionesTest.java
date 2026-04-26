package ar.edu.utn.frba.ddsi.notificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

public class ServicioDeNotificacionesTest {

    String mensaje = "Tu donación fue asignada a una entidad beneficiaria.";

    @Test
    public void notificacionEnviadaExitosamente() {
        String destinatario = "ana@mail.com";

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);

        MedioDeNotificacion medioMock = mock(MedioDeNotificacion.class);
        when(medioMock.notificar(destinatario, notificacion)).thenReturn(true);

        notificacion.setMedioDeNotificacion(medioMock);
        boolean resultado = notificacion.enviar();

        Assertions.assertTrue(resultado);
    }

    @Test
    public void notificacionEnviadaFallidamente() {
        String destinatario = "contacto@empresa.com";

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);

        MedioDeNotificacion medioMock = mock(MedioDeNotificacion.class);
        when(medioMock.notificar(destinatario, notificacion)).thenReturn(false);

        notificacion.setMedioDeNotificacion(medioMock);
        boolean resultado = notificacion.enviar();

        Assertions.assertFalse(resultado);
    }
}
