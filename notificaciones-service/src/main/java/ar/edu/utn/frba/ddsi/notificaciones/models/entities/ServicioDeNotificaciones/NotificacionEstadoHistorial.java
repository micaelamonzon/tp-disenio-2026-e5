package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Cada fila es un cambio de estado de una notificación en orden
// Notificacion.estado sigue existiendo y guarda el estado VIGENTE (para no
// tener que hacer ORDER BY cada vez que se necesita el estado actual)
// esta tabla es el historial completo para trazabilidad
@Getter
@Setter
@Entity
@Table(name = "notificacion_estado_historial")
public class NotificacionEstadoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notificacion_id", nullable = false)
    private Notificacion notificacion;

    @Enumerated(EnumType.STRING)
    @Column
    private EstadoNotificacion estado;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;
}