package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Copia local "caché" del donante de donaciones-service
// No se carga una sola vez: se pisa (upsert) cada vez que llega un evento
// con este donanteId, con los datos frescos que devuelve el GET a donaciones-service
// Así el envío siempre usa el contacto más reciente, sin necesitar un proceso de validación periódico aparte
@Getter
@Setter
@Entity
@Table(name = "donante")
public class Donante {

    // No es autoincremental: es el mismo id que donaciones-service le dio a esta PersonaHumana
    // Por eso no lleva @GeneratedValue
    @Id
    private Long id;

    @Column
    private String nombre;

    @Column
    private String apellido;

    @Column(name = "tipo_de_notificacion")
    private String tipoDeNotificacion;

    @Column
    private String contacto;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}