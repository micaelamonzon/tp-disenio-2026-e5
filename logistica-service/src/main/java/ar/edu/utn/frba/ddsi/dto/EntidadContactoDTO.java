package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record EntidadContactoDTO(
        Long id,
        String razonSocial,
        List<RepresentanteDTO> representantes
) {}