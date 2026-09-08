package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.util.List;

public record EntidadBeneficiariaDTO (
        Long id,
        String razonSocial,
        String direccion,
        String telefono,
        List<RepresentanteDTO> representantes
){}
