package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificacion {
    private String destinatario;
    private String mensaje;
    private MedioDeNotificacion medioDeNotificacion;
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    public void enviar() {
        try {
            medioDeNotificacion.notificar(this.destinatario, this);
            this.estado = EstadoNotificacion.ENVIADA;
        } catch (Exception e) {
            this.estado = EstadoNotificacion.FALLIDA;
        }
    }
}
