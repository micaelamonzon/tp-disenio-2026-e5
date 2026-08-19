package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record PlanningResultDTO (
        String requestId,
        List<RouteResultDTO> routes,
        List<UnassignedDeliveryDTO> unassignedDeliveries
){
}
