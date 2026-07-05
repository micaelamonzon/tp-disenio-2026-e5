package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record PlanRouteDTO (
        String requestId,
        TimeWindowDTO timeWindow,
        WarehouseDTO warehouse,
        List<DeliveryDTO> deliveries,
        List<TruckDTO> trucks
) {
}
