package ar.edu.utn.frba.ddsi.dto;

public record StopDTO (
        Integer stopNumber,
        String deliveryCode,
        String estimatedArrivalTimme
) {
}
