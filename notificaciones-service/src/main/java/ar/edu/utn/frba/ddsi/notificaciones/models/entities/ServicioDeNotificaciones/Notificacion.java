package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.converters.MedioDeNotificacionAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String destinatario;

    // Solo se completa cuando la notificación es para un donante
    // La entidad beneficiaria y el admin siguen usando solo destinatario
    @ManyToOne
    @JoinColumn(name = "donante_id")
    private Donante donante;

    // Id de la entidad beneficiaria si la notificación es para una entidad
    // Se usa para poder listar "las notificaciones de la entidad X" en la pantalla
    // Queda null si la notificación es para un donante o un admin
    @Column(name = "entidad_beneficiaria_id")
    private Long entidadBeneficiariaId;

    @Column(name = "donante_juridico_id")
    private Long donanteJuridicoId;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Convert(converter = MedioDeNotificacionAttributeConverter.class)
    @Column(name = "medio_de_notificacion")
    private MedioDeNotificacion medioDeNotificacion;

    @Enumerated(EnumType.STRING)
    @Column
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    public void enviar() {
        try {
            medioDeNotificacion.notificar(this.destinatario, this);
            this.estado = EstadoNotificacion.ENVIADA;
        } catch (Exception e) {
            this.estado = EstadoNotificacion.FALLIDA;
            System.out.println("Error al enviar notificación: " + e.getMessage());
        } finally {
            this.fechaEnvio = LocalDateTime.now();
        }
    }
}