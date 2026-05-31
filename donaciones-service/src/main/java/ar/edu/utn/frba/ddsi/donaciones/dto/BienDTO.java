package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.EstadoDeUso;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Unidad;

import java.time.LocalDateTime;

public record BienDTO (
        String nombre,
        String descripcion,
        SubcategoriaDTO subcategoria,
        LocalDateTime fechaDeVencimiento,
        EstadoDeUso esUsado,
        Unidad tipoUnidad,
        Integer cantidad
){
}
