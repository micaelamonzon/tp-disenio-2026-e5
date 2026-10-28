package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;

import java.time.LocalDateTime;

public record DonacionIncentivosDTO(
        Long idEnDonaciones,
        Boolean donacionEntregada,
        Long entidadBeneficiariaId,
        LocalDateTime fechaDeIngreso,
        Integer cantidadDeBienes,
        String categoria
) {
}
