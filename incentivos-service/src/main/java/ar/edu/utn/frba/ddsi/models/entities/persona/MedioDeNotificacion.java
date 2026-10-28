package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.Data;

@Data
public class MedioDeNotificacion {
    private String tipoDeNotificacion;
    private String datoDeContacto;

    public MedioDeNotificacion(String tipoDeNotificacion, String datoDeContacto) {
        this.tipoDeNotificacion = tipoDeNotificacion;
        this.datoDeContacto = datoDeContacto;
    }
}
