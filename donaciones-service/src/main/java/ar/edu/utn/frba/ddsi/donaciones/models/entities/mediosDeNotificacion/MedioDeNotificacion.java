package ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Tipo;
import lombok.Data;

@Data
public class MedioDeNotificacion{
    TipoDeNotificacion tipoDeNotificacion;
    String datoDeContacto;  /* si es Email, este dato de contacto sera el mail.
                            Si es Whatsapp o sms, este dato de cotacto sera el
                            numero de telefono*/

    public MedioDeNotificacion(TipoDeNotificacion tipoDeNotificacion, String datoDeContacto) {
        this.tipoDeNotificacion = tipoDeNotificacion;
        this.datoDeContacto = datoDeContacto;
    }


}
