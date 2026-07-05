package ar.edu.utn.frba.ddsi.dto;

public record GetRouteStatusResponseDTO (
        String requestId,
        String status,
        String createdAt,
        String updatedAt,
        RouteResultDTO result,
        RoutingErrorDTO error
) {
}
