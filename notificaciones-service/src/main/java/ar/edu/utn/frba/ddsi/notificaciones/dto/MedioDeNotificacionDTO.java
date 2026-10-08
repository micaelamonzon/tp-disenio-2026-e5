package ar.edu.utn.frba.ddsi.notificaciones.dto;

// Versión mínima del medio de notificación predeterminado.
// No necesitamos tipoDeNotificacion porque el medio a usar (EMAIL/SMS/WHATSAPP)
// ya viene en el propio NotificacionMensajeDTO (dto.medio()).
public record MedioDeNotificacionDTO(
        String tipoDeNotificacion,
        String datoDeContacto
) {}