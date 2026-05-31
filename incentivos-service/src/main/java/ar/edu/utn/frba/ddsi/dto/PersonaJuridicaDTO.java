package ar.edu.utn.frba.ddsi.dto;

import java.util.ArrayList;
import java.util.List;

public record PersonaJuridicaDTO(
        Long id,
        String cuit,
        String razonSocial,
        List<DonacionSinSegmentarDTO> donaciones
) {
}
