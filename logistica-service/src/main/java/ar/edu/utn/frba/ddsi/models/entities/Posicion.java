package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Posicion {
    private Double latitud;
    private Double longitud;
    private Instant fecha;

    public Posicion(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = Instant.now();
    }
}