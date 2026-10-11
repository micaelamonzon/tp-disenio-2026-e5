package ar.edu.utn.frba.ddsi.dtos.logistica;

import java.time.Instant;
import java.time.LocalDateTime;

public record UltimoAvanceDTO(
        PosicionDTO posicion,
        Instant fecha
) {
}
