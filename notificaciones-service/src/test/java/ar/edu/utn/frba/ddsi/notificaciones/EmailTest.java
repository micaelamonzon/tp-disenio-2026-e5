package ar.edu.utn.frba.ddsi.notificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.EmailAdapter;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmailTest {

    @Test
    void notificarDelegaEnElAdapterConLosDatosCorrectos() {
        // mockeamos el adapter para no llamar a SendGrid de verdad
        EmailAdapter adapterMock = Mockito.mock(EmailAdapter.class);
        Email email = new Email(adapterMock);

        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje("Tu entrega fue confirmada");

        email.notificar("test@donatrack.com", notificacion);

        // verificamos que Email le paso los datos correctos al adapter, sin que se haya disparado ningun envio real
        verify(adapterMock, times(1)).enviar("test@donatrack.com", "Tu entrega fue confirmada");
    }
}