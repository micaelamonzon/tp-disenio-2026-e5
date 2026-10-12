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

    public String nombreCompleto() {
        return nombre + " " + apellido;
    }

    public String iniciales() {
        String n = nombre == null || nombre.isBlank() ? "" : nombre.substring(0, 1);
        String a = apellido == null || apellido.isBlank() ? "" : apellido.substring(0, 1);
        return (n + a).toUpperCase();
    }

    public String obtenerMail(){
        return mediosDeNotificacion.stream().filter( m -> m.getTipoDeNotificacion().equals("EMAIL")).findFirst().get().getDatoDeContacto();
    }
}
