package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Tipo;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;

import java.util.ArrayList;
import java.util.List;

public record PersonaJuridicaDTO(
        Long id,
        String cuit,
        String razonSocial,
        String rubro,
        Tipo tipo,

        List<MedioDeNotificacionDTO> mediosDeNotificacion,
        List<DonacionSinSegmentarDTO> donaciones
) {
}
