package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

public abstract class EstadoDonacion {
    public abstract String getNombreEstado();

    public void asignar(DonacionSegmentada donacion) {
        throw new IllegalStateException("No se puede asignar en estado: " + getNombreEstado());
    }
    public void marcarListaParaEntregar(DonacionSegmentada donacion) {
        throw new IllegalStateException("No se puede preparar entrega en estado: " + getNombreEstado());
    }
    public void iniciarTraslado(DonacionSegmentada donacion) {
        throw new IllegalStateException("No se puede trasladar en estado: " + getNombreEstado());
    }
    public void entregar(DonacionSegmentada donacion) {
        throw new IllegalStateException("No se puede entregar en estado: " + getNombreEstado());
    }
    public void fallarEntrega(DonacionSegmentada donacion, String justificacion) {
        throw new IllegalStateException("No se puede fallar entrega en estado: " + getNombreEstado());
    }
    public void marcarVencida(DonacionSegmentada donacion) {
        throw new IllegalStateException("No se puede vencer en estado: " + getNombreEstado());
    }
}
