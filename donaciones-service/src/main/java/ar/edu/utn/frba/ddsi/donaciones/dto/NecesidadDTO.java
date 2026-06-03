package ar.edu.utn.frba.ddsi.donaciones.dto;
import lombok.Data;


public record NecesidadDTO(
        Long id,
        String tipo,
        String subcategoria,
        String descripcion,
        boolean estaSatisfecha,
        int cantidad,
        String periodo  // null si es extraordinaria
) {}