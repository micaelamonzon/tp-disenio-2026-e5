package ar.edu.utn.frba.ddsi.dtos.notificaciones;

import java.time.LocalDateTime;

// Lo que devuelve notificaciones-service en
// GET /donantes/{id}/notificaciones y GET /entidades/{id}/notificaciones.
// Los nombres tienen que coincidir con los del NotificacionResponseDTO del servicio.
public record NotificacionDTO(
        Long id,
        String mensaje,
        String medio,
        String estado,
        LocalDateTime fechaEnvio
) {}