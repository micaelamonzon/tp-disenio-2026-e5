package ar.edu.utn.frba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ruta")
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camion_id")
    private Camion camion;

    @Column(name = "fecha")
    private LocalDate fecha;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ruta_id")
    private List<PuntosDeEntrega> puntosDeEntrega;

    @Column(name = "hora_estimada_inicio")
    private LocalTime horaEstimadaInicio;

    @Column(name = "hora_estimada_fin")
    private LocalTime horaEstimadaFin;

    @Column(name = "distancia_total")
    private Integer distanciaTotal;

    @Column(name = "duracion_total_mins")
    private Integer duracionTotalMins;
}