package ar.edu.utn.frba.ddsi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeometryDTO(
        @JsonProperty("coordinates") List<List<Double>> coordenadas
) {
}
