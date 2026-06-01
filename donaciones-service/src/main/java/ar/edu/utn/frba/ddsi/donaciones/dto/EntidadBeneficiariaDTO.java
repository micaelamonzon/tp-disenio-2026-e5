package ar.edu.utn.frba.ddsi.donaciones.dto;

import lombok.Data;

import java.util.List;

public record EntidadBeneficiariaDTO (
        Long id,
        String razonSocial,
        String direccion,
        Integer telefono,
        List<RepresentanteDTO> representantes
){}
