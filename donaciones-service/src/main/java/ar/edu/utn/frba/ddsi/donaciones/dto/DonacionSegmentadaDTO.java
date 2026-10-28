package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DonacionSegmentadaDTO(
        Long id,
        String estadoActual,
        List<CambioEstadoDTO> historial,
        List<BienDTO> bienes,
        LocalDateTime fechaDeIngreso,
        LocalDateTime fechaDeAsignacion,
        Boolean donacionEntregada,
        Long organizacionId,
        String fotoPath
) {}