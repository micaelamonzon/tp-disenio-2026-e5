package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.TipoCategoria;

public record CategoriaDeMisionDTO(
        Long id,
        TipoCategoria nombre,
        TipoCategoria categoriaSiguiente
) {

}
