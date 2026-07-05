package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record PlanningResultDTO (
        List<RouteResultDTO> routes,
        List<UnassignedDeliveryDTO> unassignedDeliveries
){
}
