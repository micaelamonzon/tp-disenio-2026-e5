package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.mision.EstadoDeMision;

public record MisionDTO(
        String nombre,
        EstadoDeMision estadoDeMision
) {

}
