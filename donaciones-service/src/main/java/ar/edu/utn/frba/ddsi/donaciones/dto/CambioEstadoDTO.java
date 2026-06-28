package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.time.LocalDateTime;

public record CambioEstadoDTO(
        String estadoAnterior,
        String estadoNuevo,
        LocalDateTime fecha,
        String justificacion,
        String responsableId
) {}