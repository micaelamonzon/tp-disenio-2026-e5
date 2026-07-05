package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record RouteResultDTO (
        String truckId,
        String assignedRouteId,
        String estimatedStartTime,
        String estimatedEndTime,
        double totalDistance,
        double totalDurationMins,
        List<StopDTO> stops

) {
}
