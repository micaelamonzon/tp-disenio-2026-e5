package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.categorias.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;


public record MisionDTO(
        String nombre,
        EstadoDeMision estado,
        Categoria categoria


) {
}
