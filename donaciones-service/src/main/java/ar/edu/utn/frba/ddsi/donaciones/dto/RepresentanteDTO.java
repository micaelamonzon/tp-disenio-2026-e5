package ar.edu.utn.frba.ddsi.donaciones.dto;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;

import java.util.List;

public record RepresentanteDTO (
        String nombre,
        String apellido,
        String numeroDeDocumento,
        List<MedioDeNotificacion> mediosDeNotificacion
){}
