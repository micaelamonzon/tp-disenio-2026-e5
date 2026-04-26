package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificacion {
    private String destinatario;
    private String mensaje;
    private MedioDeNotificacion medioDeNotificacion;

    public void enviar() {
        medioDeNotificacion.notificar(this.destinatario, this);
    }
}
