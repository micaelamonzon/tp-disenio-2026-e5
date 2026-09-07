package ar.edu.utn.frba.ddsi.models.entities.misiones;

import lombok.Data;

@Data
public class AvanceDeMision {
    Integer progreso;
    Integer distanciaDelObjetivo;
    Boolean cumple;

    public AvanceDeMision(Integer progreso, Integer distanciaDelObjetivo, Boolean cumple) {
        this.progreso = progreso;
        this.distanciaDelObjetivo = distanciaDelObjetivo;
        this.cumple = cumple;
    }
}
