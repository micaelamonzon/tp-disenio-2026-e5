package ar.edu.utn.frba.ddsi.models.entities.misiones;

public class DonacionesExitosas implements Mision{

    private Integer cantidadDeDonaciones;
    private Boolean recibidaPorUnaEntidad;
    private Integer distanciaDelObjetivo; //lo que falta hasta completar la cantidad de donaciones
    private Integer progreso;
    private EstadoDeMision estado;

    public DonacionesExitosas(Integer cantidadDeDonaciones){
        this.cantidadDeDonaciones = cantidadDeDonaciones;
    }
    @Override
    public Boolean seCompletoLaMision() {
        return progreso == 100;
    }
}
