package ar.edu.utn.frba.ddsi.dtos.donaciones.misiones;

import java.util.List;

public record PerfilDTO(
        Integer cantidadDeMisionesCompletadas,
        Integer cantidadDeInsigniasGanadas,
        String nombreDeUsuario,
        List<InsigniaDTO> insignias,
        String categoriaActual
) {
}
