package ar.edu.utn.frba.ddsi.notificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.SmsAdapter;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SMSTest {

    @Test
    void notificarDelegaEnElAdapterConLosDatosCorrectos() {
        SmsAdapter adapterMock = Mockito.mock(SmsAdapter.class);
        SMS sms = new SMS(adapterMock);

        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje("Tu entrega esta en camino");

        sms.notificar("+5491133223615", notificacion);

        verify(adapterMock, times(1)).enviar("+5491133223615", "Tu entrega esta en camino");
    }
}