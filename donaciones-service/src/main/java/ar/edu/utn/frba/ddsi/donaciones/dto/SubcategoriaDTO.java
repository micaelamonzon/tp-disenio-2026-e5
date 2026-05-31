package ar.edu.utn.frba.ddsi.donaciones.dto;

public record SubcategoriaDTO(
        String nombre,
        boolean esPerecedero,
        CategoriaDTO categoria
) {
}
