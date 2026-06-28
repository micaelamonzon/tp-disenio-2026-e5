package ar.edu.utn.frba.ddsi.donaciones;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DonacionSegmentadaTest {

    private DonacionSegmentada donacionEquis() {
        return new DonacionSegmentada(new Subcategoria("Arroz", false, null));
    }

    // Cuando creo una donacion el estado inicial debe ser EN_DEPOSITO
    @Test
    void unaDonacionNaceEnDeposito() {
        DonacionSegmentada donacion = donacionEquis();
        assertEquals("EN_DEPOSITO", donacion.getNombreEstadoActual());
        assertEquals(1, donacion.getHistorial().size());
    }

    // Desde EN_DEPOSITO se puede pasar a ASIGNACION_REALIZADA
    @Test
    void sePuedeAsignarDesdeDeposito() {
        DonacionSegmentada donacion = donacionEquis();
        donacion.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA, "Asignada", "admin1");
        assertEquals("ASIGNACION_REALIZADA", donacion.getNombreEstadoActual());
        assertEquals(2, donacion.getHistorial().size());
    }

    // Camino feliz :D
    @Test
    void flujoCompletoHastaEntregada() {
        DonacionSegmentada donacion = donacionEquis();
        donacion.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA, "Asignada", "admin1");
        donacion.cambiarEstado(EstadoDonacion.LISTA_PARA_ENTREGAR, "Ruta planificada", "admin1");
        donacion.cambiarEstado(EstadoDonacion.EN_TRASLADO, "Camión salió", "chofer1");
        donacion.cambiarEstado(EstadoDonacion.ENTREGADA, "Confirmada", "entidad1");
        assertEquals("ENTREGADA", donacion.getNombreEstadoActual());
    }

    // Si falla, tiene que volver al depósito
    @Test
    void entregaFallidaVuelveAlDeposito() {
        DonacionSegmentada donacion = donacionEquis();
        donacion.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA, "Asignada", "admin1");
        donacion.cambiarEstado(EstadoDonacion.LISTA_PARA_ENTREGAR, "Ruta planificada", "admin1");
        donacion.cambiarEstado(EstadoDonacion.EN_TRASLADO, "Camión salió", "chofer1");
        donacion.cambiarEstado(EstadoDonacion.ENTREGA_FALLIDA,
                "Tocamos timbre pero no hay nadie", "chofer1");
        donacion.cambiarEstado(EstadoDonacion.EN_DEPOSITO,
                "Retorno al depósito", "SISTEMA");
        assertEquals("EN_DEPOSITO", donacion.getNombreEstadoActual());
    }

    // Tiene que saltar excepción ante transición inválida
    @Test
    void noSePuedeEntregarSinIniciarTraslado() {
        DonacionSegmentada donacion = donacionEquis();
        donacion.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA, "Asignada", "admin1");
        assertThrows(IllegalStateException.class, () ->
                donacion.cambiarEstado(EstadoDonacion.ENTREGADA, "inválido", "admin1")
        );
    }

    // El motivo de la falla se guarda en el historial
    @Test
    void elHistorialRegistraLaJustificacionDeFallo() {
        DonacionSegmentada donacion = donacionEquis();
        donacion.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA, "Asignada", "admin1");
        donacion.cambiarEstado(EstadoDonacion.LISTA_PARA_ENTREGAR, "Ruta planificada", "admin1");
        donacion.cambiarEstado(EstadoDonacion.EN_TRASLADO, "Camión salió", "chofer1");
        donacion.cambiarEstado(EstadoDonacion.ENTREGA_FALLIDA, "Dirección incorrecta", "chofer1");

        boolean tieneJustificacion = donacion.getHistorial().stream()
                .anyMatch(c -> c.getEstadoNuevo().equals("ENTREGA_FALLIDA")
                        && c.getJustificacion().equals("Dirección incorrecta"));
        assertTrue(tieneJustificacion);
    }

    // El responsable queda registrado en el historial
    @Test
    void elHistorialRegistraElResponsable() {
        DonacionSegmentada donacion = donacionEquis();
        donacion.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA, "Asignada", "admin1");
        boolean tieneResponsable = donacion.getHistorial().stream()
                .anyMatch(c -> "admin1".equals(c.getResponsableId()));
        assertTrue(tieneResponsable);
    }
}
