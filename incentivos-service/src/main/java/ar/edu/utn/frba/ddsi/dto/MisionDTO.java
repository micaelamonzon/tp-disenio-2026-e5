package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;

import java.util.List;

public record MisionDTO(
        Integer cantidadDeBienes,
        Integer cantidadDeDonaciones,
        Boolean recibidaPorUnaEntidad,
        List<CategoriaDTO> categorias,
        Integer meses,
        Integer distanciaDelObjetivo,
        Integer progreso,
        EstadoDeMision estado
) {
}
