package ar.edu.utn.frba.ddsi.notificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.adapters.WhatsAppAdapter;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WhatsAppTest {

    @Test
    void notificarDelegaEnElAdapterConLosDatosCorrectos() {
        WhatsAppAdapter adapterMock = Mockito.mock(WhatsAppAdapter.class);
        WhatsApp whatsApp = new WhatsApp(adapterMock);

        Notificacion notificacion = new Notificacion();
        notificacion.setMensaje("Entrega no satisfactoria");

        whatsApp.notificar("+5491133223615", notificacion);

        verify(adapterMock, times(1)).enviar("+5491133223615", "Entrega no satisfactoria");
    }
}