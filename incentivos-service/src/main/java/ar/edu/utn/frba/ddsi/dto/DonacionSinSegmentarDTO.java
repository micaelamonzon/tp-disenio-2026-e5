package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DonacionSinSegmentarDTO(
        List<DonacionSegmentadaDTO> bienesDelMismoTipo,
        SubcategoriaDTO subcategoria,
        LocalDateTime fechaDeDonacion
)
{
}
