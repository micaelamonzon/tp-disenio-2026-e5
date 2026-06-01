package ar.edu.utn.frba.ddsi.donaciones.models.entities.mision;


import lombok.Data;

@Data
public class Mision {
    String nombre;
    EstadoDeMision estadoDeMision;

    public Mision(String nombre,EstadoDeMision estadoDeMision) {
        this.nombre = nombre;
        this.estadoDeMision = estadoDeMision;
    }
}
