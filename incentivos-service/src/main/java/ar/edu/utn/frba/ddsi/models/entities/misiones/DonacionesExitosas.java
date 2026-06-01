package ar.edu.utn.frba.ddsi.models.entities.misiones;

public class DonacionesExitosas{

    private Integer cantidadDeDonaciones;
    private Boolean recibidaPorUnaEntidad;
    private Integer distanciaDelObjetivo; //lo que falta hasta completar la cantidad de donaciones
    private Integer progreso;
    private EstadoDeMision estado;

    public DonacionesExitosas(Integer cantidadDeDonaciones){
        this.cantidadDeDonaciones = cantidadDeDonaciones;
    }

    public Boolean seCompletoLaMision() {
        return progreso == 100;
    }
}
