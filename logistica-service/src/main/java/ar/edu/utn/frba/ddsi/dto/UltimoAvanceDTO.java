package ar.edu.utn.frba.ddsi.dto;

import java.time.Instant;

public record UltimoAvanceDTO(
        PosicionDTO posicion,
        Instant fecha
) {
}
