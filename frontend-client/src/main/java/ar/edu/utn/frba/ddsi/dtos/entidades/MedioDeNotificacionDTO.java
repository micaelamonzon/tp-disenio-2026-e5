package ar.edu.utn.frba.ddsi.dtos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedioDeNotificacionDTO {
    private String tipoDeNotificacion; // EMAIL, SMS, WHATSAPP
    private String datoDeContacto;
}
