package ar.edu.utn.frba.ddsi.models.entities.misiones;

public class HabilDonador implements Mision {

    private Integer cantidadDeBienes;
    private Integer distanciaDelObjetivo;
    private Integer progreso;
    private EstadoDeMision estado;

    public HabilDonador(Integer cantidadDeBienes){
        this.cantidadDeBienes = cantidadDeBienes;
    }

    @Override
    public Boolean seCompletoLaMision() {
        //donación que supere X cantidad de bienes.
        return progreso == 100;
    }

}
