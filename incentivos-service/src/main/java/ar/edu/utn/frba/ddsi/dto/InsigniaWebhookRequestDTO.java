package ar.edu.utn.frba.ddsi.dto;

import lombok.Data;

public record InsigniaWebhookRequestDTO(
        String nombreDonante,
        String nombreInsignia
){}
