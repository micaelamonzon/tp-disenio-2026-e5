package ar.edu.utn.frba.ddsi.donaciones.dto;
import lombok.Data;
import java.util.List;


public class PosibleDonacionDTO {
    private Long id;
    private String estadoActual;
    private List<CambioEstadoRequestDTO> historial;
}
