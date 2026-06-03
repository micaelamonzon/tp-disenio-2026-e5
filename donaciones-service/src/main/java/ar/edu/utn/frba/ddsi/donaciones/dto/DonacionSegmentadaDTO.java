package ar.edu.utn.frba.ddsi.donaciones.dto;
import lombok.Data;

import java.util.List;
public record DonacionSegmentadaDTO(
        Long id,
        String estadoActual,
        List<CambioEstadoDTO> historial
) {
}