package ar.edu.utn.frba.ddsi.dtos.logistica;

import java.util.List;

public record RutaEntregaDTO(
        String entregaId,
        String estadoActual,
        String direccion,
        PosicionDTO destino,
        List<PosicionDTO> rutaCompleta,
        List<String> subcategorias,
        UltimoAvanceDTO ultimoAvance
) {
}
