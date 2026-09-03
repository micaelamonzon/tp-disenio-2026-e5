package ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion;

import lombok.Data;

@Data
public class MedioDeNotificacion{
    TipoDeNotificacion tipoDeNotificacion;
    String contacto;  /* si es Email, este dato de contacto sera el mail.
                            Si es Whatsapp o sms, este dato de cotacto sera el
                            numero de telefono*/

    public MedioDeNotificacion(TipoDeNotificacion tipoDeNotificacion, String datoDeContacto) {
        this.tipoDeNotificacion = tipoDeNotificacion;
        this.contacto = datoDeContacto;
    }


}
