package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DonacionSinSegmentarDTO(
        List<BienDTO> bienes,
        LocalDateTime fechaDeIngreso,
        Boolean donacionEntregada
)
{
}
