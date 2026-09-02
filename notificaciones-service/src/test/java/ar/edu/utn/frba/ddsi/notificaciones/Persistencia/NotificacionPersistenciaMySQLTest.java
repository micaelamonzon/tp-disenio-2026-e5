package ar.edu.utn.frba.ddsi.notificaciones.Persistencia;

import ar.edu.utn.frba.ddsi.notificaciones.controllers.controllerDeNotificaciones;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.EstadoNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Test contra la base MySQL real (sin @ActiveProfiles("test")), usando el application.properties
// Sirve para demostrar la persistencia contra la base real, no solo contra H2
// No limpia automaticamente: la fila queda visible en Workbench para mostrarla
// Correr limpiar() a demanda cuando ya se mostro

@SpringBootTest
class NotificacionPersistenciaMySQLTest {

    @Autowired
    private controllerDeNotificaciones controller;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Test
    void alNotificarSePersisteEnLaBaseMySQLReal() {
        controller.notificar("test-mysql@donatrack.com", "Prueba contra MySQL real", "EMAIL");

        List<Notificacion> guardadas = notificacionRepository
                .findAll()
                .stream()
                .filter(n -> "test-mysql@donatrack.com".equals(n.getDestinatario()))
                .toList();

        assertThat(guardadas).hasSize(1);
        Notificacion notificacion = guardadas.get(0);
        assertThat(notificacion.getMensaje()).isEqualTo("Prueba contra MySQL real");
        assertThat(notificacion.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
    }

    @Test
    void limpiar() {
        List<Notificacion> paraBorrar = notificacionRepository
                .findAll()
                .stream()
                .filter(n -> "test-mysql@donatrack.com".equals(n.getDestinatario()))
                .toList();
        notificacionRepository.deleteAll(paraBorrar);
    }
}