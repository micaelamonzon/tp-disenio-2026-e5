package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

// Representante de una entidad beneficiaria, con sus medios de contacto
public record RepresentanteDTO(
        String nombre,
        String apellido,
        List<MedioDeNotificacionDTO> mediosDeNotificacion
) {}