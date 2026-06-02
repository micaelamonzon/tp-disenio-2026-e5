package ar.edu.utn.frba.ddsi.donaciones.dto;
import lombok.Data;
import java.util.List;

@Data
public class PosibleDonacionDTO {
    private Long id;
    private String estadoActual;
    private List<CambioEstadoRequestDTO> historial;
}
