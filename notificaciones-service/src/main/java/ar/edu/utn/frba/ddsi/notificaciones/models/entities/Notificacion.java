package ar.edu.utn.frba.ddsi.notificaciones.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificacion {
    private String destinatario;
    private String mensaje;
    private MedioDeNotificacion medioDeNotificacion;
}
