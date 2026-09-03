package ar.edu.utn.frba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "posicion")
public class Posicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    public Posicion(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
}