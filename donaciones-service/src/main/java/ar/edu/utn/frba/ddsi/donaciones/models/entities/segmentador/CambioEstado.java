package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cambio_estado")
public class CambioEstado extends Persistente {
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoDonacion estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoDonacion estadoNuevo;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "justificacion")
    private String justificacion;    // el "por qué"

    @Column(name = "responsable_id")
    private String responsableId;    // el "quién" — puede ser userId o nombre

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_donante")
    private TipoDonante tipoDonante;

    @ManyToOne
    @JoinColumn(name = "donacion_segmentada_id", nullable = false)
    private DonacionSegmentada donacionSegmentada;
    public CambioEstado(EstadoDonacion anterior, EstadoDonacion nuevo,
                        String justificacion, String responsableId) {
        this.estadoAnterior = anterior != null ? anterior : EstadoDonacion.INICIO;
        this.estadoNuevo = nuevo;
        this.fecha = LocalDateTime.now();
        this.justificacion = justificacion;
        this.responsableId = responsableId;
    }
}