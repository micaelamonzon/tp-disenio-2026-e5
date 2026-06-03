package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.TipoDeNotificacion;

public record MedioDeNotificacionDTO(
        TipoDeNotificacion tipoDeNotificacion,
        String datoDeContacto
) {
}