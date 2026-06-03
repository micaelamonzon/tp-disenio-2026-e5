package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public class AsignacionRealizada extends EstadoDonacion {
    @Override public String getNombreEstado() { return "ASIGNACION_REALIZADA"; }

    @Override
    public void marcarListaParaEntregar(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new ListaParaEntregar(), "Ruta planificada");
    }

    @Override
    public void marcarVencida(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new Vencida(), "Marcada como vencida por administrador");
    }
}