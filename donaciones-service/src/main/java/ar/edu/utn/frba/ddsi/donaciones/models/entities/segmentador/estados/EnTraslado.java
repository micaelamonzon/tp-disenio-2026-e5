package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public class EnTraslado extends EstadoDonacion {
    @Override public String getNombreEstado() { return "EN_TRASLADO"; }

    @Override
    public void entregar(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new Entregada(), "Entidad beneficiaria confirmó recepción");
    }

    @Override
    public void fallarEntrega(DonacionSegmentada donacion, String justificacion) {
        donacion.cambiarEstado(new EntregaFallida(), justificacion);
        donacion.cambiarEstado(new EnDeposito(), "Retorno automático al depósito");
    }
}