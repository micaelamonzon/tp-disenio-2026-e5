package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

public abstract class EstadoDonacion {
    public abstract String getNombreEstado();

    public void asignar(PosibleDonacion donacion) {
        throw new IllegalStateException("No se puede asignar en estado: " + getNombreEstado());
    }
    public void marcarListaParaEntregar(PosibleDonacion donacion) {
        throw new IllegalStateException("No se puede preparar entrega en estado: " + getNombreEstado());
    }
    public void iniciarTraslado(PosibleDonacion donacion) {
        throw new IllegalStateException("No se puede trasladar en estado: " + getNombreEstado());
    }
    public void entregar(PosibleDonacion donacion) {
        throw new IllegalStateException("No se puede entregar en estado: " + getNombreEstado());
    }
    public void fallarEntrega(PosibleDonacion donacion, String justificacion) {
        throw new IllegalStateException("No se puede fallar entrega en estado: " + getNombreEstado());
    }
    public void marcarVencida(PosibleDonacion donacion) {
        throw new IllegalStateException("No se puede vencer en estado: " + getNombreEstado());
    }
}
