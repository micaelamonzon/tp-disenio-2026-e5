package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.persona.EstadoDeUso;
import ar.edu.utn.frba.ddsi.models.entities.persona.Unidad;

import java.time.LocalDateTime;

public record BienDTO(
        String nombre,
        String descripcion,
        SubcategoriaDTO subcategoria,
        LocalDateTime fechaDeVencimiento,
        EstadoDeUso esUsado,
        Unidad tipoUnidad,
        Integer cantidad
) {
}
