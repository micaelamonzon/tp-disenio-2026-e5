package ar.edu.utn.frba.ddsi.notificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificacionEventosTest {

    String personaDonanteEmail = "eperezarevalos@frba.utn.edu.ar";
    String entidadBeneficiaria = "+18777804236";
    String personaDonanteWapp = "+5491133223615";

    @Autowired
    private Email email;

    @Autowired
    private SMS sms;

    @Autowired
    private WhatsApp whatsApp;

    // Evento 1: Donante inactivo más de 20 días
    @Test
    public void notificarDonanteInactivo() {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(personaDonanteEmail);
        notificacion.setMensaje("Hace más de 20 días que no donás. Te esperamos nuevamente!");
        notificacion.setMedioDeNotificacion(email);
        notificacion.enviar();
        Assertions.assertEquals(EstadoNotificacion.ENVIADA, notificacion.getEstado());
    }

    // Evento 2: EF recibe una donación asignada
    @Test
    public void notificarEntidadBeneficiariaConDonacionAsignada() {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(entidadBeneficiaria);
        notificacion.setMensaje("Se le asigno una nueva donacion a su entidad beneficiaria");
        notificacion.setMedioDeNotificacion(sms);
        notificacion.enviar();
        Assertions.assertEquals(EstadoNotificacion.ENVIADA, notificacion.getEstado());
    }

    // Evento 3: Donante notificado cuando su donación fue asignada a una EF
    @Test
    public void notificarDonanteConDonacionAsignada() {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(personaDonanteWapp);
        notificacion.setMensaje("Tu donacion fue asignada a una Entidad Beneficiaria");
        notificacion.setMedioDeNotificacion(whatsApp);
        notificacion.enviar();
        Assertions.assertEquals(EstadoNotificacion.ENVIADA, notificacion.getEstado());
    }

    // Evento 4: Donante cumplió una misión
    @Test
    public void notificarDonanteCumpliMision() {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(personaDonanteEmail);
        notificacion.setMensaje("Felicitaciones! Completaste la misión Donaciones Exitosas");
        notificacion.setMedioDeNotificacion(email);
        notificacion.enviar();
        Assertions.assertEquals(EstadoNotificacion.ENVIADA, notificacion.getEstado());
    }

    // Evento 5: Donante cambió de categoría
    @Test
    public void notificarDonanteCambioDeCategoria() {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(personaDonanteEmail);
        notificacion.setMensaje("Subiste de categoría! Ahora sos donante Sostenedor");
        notificacion.setMedioDeNotificacion(email);
        notificacion.enviar();
        Assertions.assertEquals(EstadoNotificacion.ENVIADA, notificacion.getEstado());
    }
}