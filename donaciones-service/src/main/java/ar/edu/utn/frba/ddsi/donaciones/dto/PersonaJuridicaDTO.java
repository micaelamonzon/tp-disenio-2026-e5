package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Tipo;

import java.util.List;

public record PersonaJuridicaDTO(
        Long id,
        String cuit,
        String razonSocial,
        String rubro,
        Tipo tipo,

        List<RepresentanteDTO> representantes,
        List<MedioDeNotificacionDTO> mediosDeNotificacion,
        List<DonacionSinSegmentarDTO> donaciones
) {
}
