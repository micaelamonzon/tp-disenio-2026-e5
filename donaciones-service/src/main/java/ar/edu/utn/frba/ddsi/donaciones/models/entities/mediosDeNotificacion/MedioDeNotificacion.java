package ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medio_de_notificacion")
public class MedioDeNotificacion extends Persistente {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_notificacion", nullable = false)
    TipoDeNotificacion tipoDeNotificacion;

    @Column(name = "contacto")
    String contacto;  /* si es Email, este dato de contacto sera el mail.
                            Si es Whatsapp o sms, este dato de cotacto sera el
                            numero de telefono*/

    public MedioDeNotificacion(TipoDeNotificacion tipoDeNotificacion, String datoDeContacto) {
        this.tipoDeNotificacion = tipoDeNotificacion;
        this.contacto = datoDeContacto;
    }


}
