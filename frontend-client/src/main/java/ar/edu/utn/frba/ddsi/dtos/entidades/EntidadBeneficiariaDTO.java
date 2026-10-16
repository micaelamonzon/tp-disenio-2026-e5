package ar.edu.utn.frba.ddsi.dtos.entidades;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.Objects;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntidadBeneficiariaDTO {
    private Long id;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private List<RepresentanteDTO> representantes;


    public String iniciales() {
        if (razonSocial == null || razonSocial.isBlank()) {
            return "";
        }
        String[] palabras = razonSocial.trim().split("\\s+");
        if (palabras.length == 1) {
            return palabras[0].substring(0, Math.min(2, palabras[0].length())).toUpperCase();
        }
        return ("" + palabras[0].charAt(0) + palabras[1].charAt(0)).toUpperCase();
    }

    @JsonIgnore
    public List<String> getCorreosRepresentantes() {
        if (representantes == null) {
            return List.of();
        }
        return representantes.stream()
                .map(RepresentanteDTO::obtenerMail)
                .filter(Objects::nonNull)
                .toList();
    }
}