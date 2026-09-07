package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.time.LocalDateTime;

public record DonacionIncentivosDTO(
    Boolean donacionEntregada,
    Long entidadBeneficiariaId,
    LocalDateTime fechaDeIngreso,
    Integer cantidadDeBienes,
    String categoria
) {
}
