package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;

//DTO EXCLUSIVAMENTE PARA QUE LO USEMOS CON LOGISTICA !!!
public record DonacionDTO(
        Integer codigo,
        TipoDonante tipoDonante,
        String estado,
        Double pesoKg,
        Double volumenM3,
        Long entidadBeneficiariaId,
        Long donanteId,
        String direccionEntidad,
        Double latitudEntidad,
        Double longitudEntidad,
        String subcategoria
) {
}
