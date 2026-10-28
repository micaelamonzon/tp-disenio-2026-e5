package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;

public record MisionParticularDTO(
        String nombre,
        EstadoDeMision estado
) {
}
