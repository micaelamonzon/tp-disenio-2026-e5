package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public class EnDeposito extends EstadoDonacion {
    @Override public String getNombreEstado() { return "EN_DEPOSITO"; }

    @Override
    public void asignar(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new AsignacionRealizada(), "Asignada a entidad beneficiaria");
    }

    @Override
    public void marcarVencida(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new Vencida(), "Marcada como vencida por administrador");
    }
}