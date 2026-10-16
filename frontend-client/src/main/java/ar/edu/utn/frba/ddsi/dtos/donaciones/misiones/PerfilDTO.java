package ar.edu.utn.frba.ddsi.dtos.donaciones.misiones;

import java.util.List;

public record PerfilDTO(
        Integer cantidadDeMisionesCompletadas,
        Integer cantidadDeInsigniasGanadas,
        String nombreDeUsuario,
        List<InsigniaDTO> insignias,
        String categoriaActual,
        Integer puestoRanking
) {

    public String iniciales() {
        if (nombreDeUsuario == null || nombreDeUsuario.isBlank()) {
            return "";
        }
        java.util.Set<String> ignoradas = java.util.Set.of("de", "del", "la", "las", "los", "el", "y");
        StringBuilder sb = new StringBuilder();
        for (String p : nombreDeUsuario.trim().split("\\s+")) {
            if (!ignoradas.contains(p.toLowerCase())) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() == 2) break;
            }
        }
        return sb.toString();
    }
}
