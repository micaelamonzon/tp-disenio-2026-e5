package ar.edu.utn.frba.ddsi.donaciones.dto;


import java.util.List;

public record RepresentanteDTO (
        String nombre,
        String apellido,
        Integer numeroDeDocumento,
        List<MedioDeNotificacionDTO> mediosDeNotificacion
){}
