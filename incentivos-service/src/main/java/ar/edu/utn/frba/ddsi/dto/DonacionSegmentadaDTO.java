package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;


public record DonacionSegmentadaDTO(
        Long id,
        List<BienDTO> bienes,
        LocalDateTime fechaDeIngreso,
        Boolean donacionEntregada,
        Long organizacionId

) {}