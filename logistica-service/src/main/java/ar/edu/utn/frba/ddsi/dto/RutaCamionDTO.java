package ar.edu.utn.frba.ddsi.dto;

import java.util.List;
import java.util.Map;

public record RutaCamionDTO(
        List<PosicionDTO> ruta,
        Map<String, Integer> indicePorEntrega
) {
    public Integer indiceDe(String entregaId) {
        return indicePorEntrega.get(entregaId);
    }
}
