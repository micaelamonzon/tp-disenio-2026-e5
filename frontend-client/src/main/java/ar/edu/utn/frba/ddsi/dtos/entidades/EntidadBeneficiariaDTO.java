package ar.edu.utn.frba.ddsi.dtos.entidades;
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

    public List<String> getCorreosRepresentantes() {
        if (representantes == null) {
            return List.of();
        }
        return representantes.stream()
                .filter(r -> r.getMediosDeNotificacion() != null)
                .flatMap(r -> r.getMediosDeNotificacion().stream())
                .filter(m -> "EMAIL".equals(m.getTipoDeNotificacion()))
                .map(MedioDeNotificacionDTO::getDatoDeContacto)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}