package ar.edu.utn.frba.ddsi.models.entities.misiones;

public class Racha {

    private Integer meses;
    private Integer distanciaDelObjetivo;
    private Integer progreso;
    private EstadoDeMision estado;

    public Racha(Integer meses){
        this.meses = meses;
    }

    public Boolean seCompletoLaMision() {
        // pedirle al servicio de donaciones,
        // todas las donaciones de la persona donante y que se hayan hecho durante los x meses consecutivos

        return progreso == 100;
    }
    public Integer bajarProgreso(Boolean respuesta){
        if(respuesta){
            return this.progreso -= (100/meses);
        }else{
            return this.progreso;
        }
    }
}
