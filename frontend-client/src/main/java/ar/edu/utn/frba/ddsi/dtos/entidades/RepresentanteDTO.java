package ar.edu.utn.frba.ddsi.dtos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepresentanteDTO {
    private String nombre;
    private String apellido;
    private List<MedioDeNotificacionDTO> mediosDeNotificacion;
}
