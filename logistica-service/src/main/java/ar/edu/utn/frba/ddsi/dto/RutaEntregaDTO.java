package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.Posicion;

import java.util.List;

public record RutaEntregaDTO(
        String entregaId,
        String camionId,
        String estadoActual,
        String direccion,
        PosicionDTO destino,
        List<PosicionDTO> rutaCompleta,   // ruta del camión, compartida por todas sus entregas
        Integer indiceParada,             // posición de esta entrega dentro de rutaCompleta
        List<String> subcategorias,
        UltimoAvanceDTO ultimoAvance
) {
}
