package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Posicion {
    private Double latitud;
    private Double longitud;

    public Posicion(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
}