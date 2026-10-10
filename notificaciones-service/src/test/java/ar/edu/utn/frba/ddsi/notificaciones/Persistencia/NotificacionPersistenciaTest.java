package ar.edu.utn.frba.ddsi.notificaciones.Persistencia;

import ar.edu.utn.frba.ddsi.notificaciones.controllers.controllerDeNotificaciones;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.EstadoNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// @ActiveProfiles("test") activa los adapters Fake (no manda mail/SMS real)
// y la base H2 en memoria configurada en application-test.properties
@SpringBootTest
@ActiveProfiles("test")
class NotificacionPersistenciaTest {

    @Autowired
    private controllerDeNotificaciones controller;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Test
    void alNotificarSePersisteLaNotificacionConEstadoEnviada() {
        // Llama directo al método del controller, como haría Postman
        //controller.notificar("test@donatrack.com", "Prueba de persistencia", "EMAIL");
        controller.notificar("test-mysql@donatrack.com", "Prueba contra MySQL real", "EMAIL", null, null);

        List<Notificacion> guardadas = notificacionRepository.findAll();

        assertThat(guardadas).hasSize(1);
        Notificacion notificacion = guardadas.get(0);
        assertThat(notificacion.getDestinatario()).isEqualTo("test@donatrack.com");
        assertThat(notificacion.getMensaje()).isEqualTo("Prueba de persistencia");
        assertThat(notificacion.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(notificacion.getFechaEnvio()).isNotNull();
    }
}