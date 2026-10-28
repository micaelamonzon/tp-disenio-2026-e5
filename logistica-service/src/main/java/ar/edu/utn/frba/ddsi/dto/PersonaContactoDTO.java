package ar.edu.utn.frba.ddsi.dto;

public record PersonaContactoDTO(
        Long id,
        String nombre,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
) {}