package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record PerfilDTO(Integer cantidadDeMisionesCompletadas,
                        Integer cantidadDeInsigniasGanadas,
                        String nombreDeUsuario,
                        List<InsigniaDTO> insignias,
                        String categoriaActual) {
}
