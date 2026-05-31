package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;

public record SubcategoriaDTO(
        String nombre,
        boolean esPerecedero,
        CategoriaDTO categoria
) {
}
