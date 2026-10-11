package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.services.Impl.OsrmRutaService;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OsmrResponseDTO(
        @JsonProperty("code") String codigo,
        @JsonProperty("routes") List<RutaDTO> ruta
) {
}
