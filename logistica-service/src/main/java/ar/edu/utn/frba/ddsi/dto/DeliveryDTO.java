package ar.edu.utn.frba.ddsi.dto;

public record DeliveryDTO(
        Integer deliveryCode,
        Double latitude,
        Double longitude,
        String address,
        Double weightKg,
        Double volumeM3
) {
}
