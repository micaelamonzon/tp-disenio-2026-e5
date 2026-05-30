package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.Data;

@Data
public class MedioDeNotificacion {
    TipoDeNotificacion tipoDeNotificacion;
    String datoDeContacto;

    public MedioDeNotificacion(TipoDeNotificacion tipoDeNotificacion, String datoDeContacto) {
        this.tipoDeNotificacion = tipoDeNotificacion;
        this.datoDeContacto = datoDeContacto;
    }
}
