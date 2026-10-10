package ar.edu.utn.frba.ddsi.dto;

public record DeliveryDTO(
        String deliveryCode,
        Double latitude,
        Double longitude,
        String address,
        Double WeightKg,
        Double VolumeM3
) {
}
