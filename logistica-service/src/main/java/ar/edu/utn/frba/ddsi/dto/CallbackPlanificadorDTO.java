package ar.edu.utn.frba.ddsi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CallbackPlanificadorDTO(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("request_id") String requestId,
        String timestamp,
        PlanningResultDTO data,
        RoutingErrorDTO error
) {
}
