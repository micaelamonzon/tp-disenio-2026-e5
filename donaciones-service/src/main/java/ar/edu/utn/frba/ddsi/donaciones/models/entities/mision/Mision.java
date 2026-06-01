package ar.edu.utn.frba.ddsi.donaciones.models.entities.mision;


import lombok.Data;

@Data
public class Mision {
    String nombre;

    public Mision(String nombre) {
        this.nombre = nombre;
    }
}
