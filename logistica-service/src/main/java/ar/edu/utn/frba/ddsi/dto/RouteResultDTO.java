package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record RouteResultDTO (
        String truckId,
        String assignedRouteId,
        String estimatedStartTime,
        String estimatedEndTime,
        Double totalDistanceKm,
        Integer totalDurationMins,
        List<StopDTO> stops

) {
}
