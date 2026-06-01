package ar.edu.utn.frba.ddsi.models.entities.misiones;

public class HabilDonador {

    private String nombre;
    private Integer cantidadDeBienes;
    private Integer distanciaDelObjetivo;
    private Integer progreso;
    private EstadoDeMision estado;

    public HabilDonador(Integer cantidadDeBienes){
        this.cantidadDeBienes = cantidadDeBienes;
    }


    public Boolean seCompletoLaMision() {
        //donación que supere X cantidad de bienes.
        return progreso == 100;
    }

}
