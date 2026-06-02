package ar.edu.utn.frba.ddsi.donaciones.dto;

public record SubcategoriaDTO(
        String nombre,
        Boolean esPerecedero,
        CategoriaDTO categoria
) {
}
