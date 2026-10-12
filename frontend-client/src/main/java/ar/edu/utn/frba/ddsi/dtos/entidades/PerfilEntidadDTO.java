package ar.edu.utn.frba.ddsi.dtos.entidades;

import java.util.List;

public record PerfilEntidadDTO(
        Long id,
        String razonSocial,
        String direccion,
        String telefono,
        List<RepresentanteDTO> representantes
) {
}
