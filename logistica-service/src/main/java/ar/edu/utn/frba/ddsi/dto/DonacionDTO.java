package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.TipoDeDonante;

import java.time.LocalDateTime;
import java.util.List;

public record DonacionDTO(
        Integer codigo, // es el id que tiene el donaciones
        TipoDeDonante tipoDonante,
        String estado,
        Double pesoKg,
        Double volumenM3,
        Long entidadBeneficiariaId,
        Long donanteId,
        String direccionEntidad,
        double latitudEntidad,
        double longitudEntidad,
        String subcategoria
) {
}
