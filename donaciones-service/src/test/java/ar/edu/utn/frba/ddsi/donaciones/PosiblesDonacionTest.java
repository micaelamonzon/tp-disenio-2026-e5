package ar.edu.utn.frba.ddsi.donaciones;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.PosibleDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PosibleDonacionTest {

    private PosibleDonacion donacionEquis() {
        Subcategoria sub = new Subcategoria("Arroz", false,null);
        return new PosibleDonacion(sub);
    }
// Cuando creo un donacion el estado inicial debe ser En_Deposito
    @Test
    void unaDonacionNaceEnDeposito() {
        PosibleDonacion donacion = donacionEquis();

        assertEquals("EN_DEPOSITO", donacion.getNombreEstadoActual());
        assertEquals(1, donacion.getHistorial().size()); // el registro inicial
    }
//En Deposito se puede llamar a la funcion asignar y le puede cambiar el estado
    @Test
    void sePuedeAsignarDesdeDeposito() {
        PosibleDonacion donacion = donacionEquis();

        donacion.asignar();

        assertEquals("ASIGNACION_REALIZADA", donacion.getNombreEstadoActual());
        assertEquals(2, donacion.getHistorial().size());
    }
//camino feliiiiz :D
    @Test
    void flujoCompletoHastaEntregada() {
        PosibleDonacion donacion = donacionEquis();

        donacion.asignar();
        donacion.marcarListaParaEntregar();
        donacion.iniciarTraslado();
        donacion.entregar();

        assertEquals("ENTREGADA", donacion.getNombreEstadoActual());
    }
//Si falla, tiene que volver al deposito
    @Test
    void entregaFallidaVuelveAlDeposito() {
        PosibleDonacion donacion = donacionEquis();

        donacion.asignar();
        donacion.marcarListaParaEntregar();
        donacion.iniciarTraslado();
        donacion.fallarEntrega("Tocamos timbre pero no hay nadie");

        assertEquals("EN_DEPOSITO", donacion.getNombreEstadoActual());
    }
//tiene que saltar una excepcion ante actividad rara
    @Test
    void noSePuedeEntregarSinIniciarTraslado() {
        PosibleDonacion donacion = donacionEquis();

        donacion.asignar();

        assertThrows(IllegalStateException.class, () -> donacion.entregar());
    }
    //el motivo de la falla se guarda en el historial
    @Test
    void elHistorialRegistraLaJustificacionDeFallo() {
        PosibleDonacion donacion = donacionEquis();
        donacion.asignar();
        donacion.marcarListaParaEntregar();
        donacion.iniciarTraslado();
        donacion.fallarEntrega("Dirección incorrecta");

        boolean tieneJustificacion = donacion.getHistorial().stream()
                .anyMatch(c -> c.getEstadoNuevo().equals("ENTREGA_FALLIDA")
                        && c.getJustificacion().equals("Dirección incorrecta"));

        assertTrue(tieneJustificacion);
    }
}
