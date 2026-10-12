package ar.edu.utn.frba.ddsi.donaciones.dto;

// Espejo del DTO que consume notificaciones-service desde la cola.
// Los nombres de los campos tienen que coincidir para que el JSON se lea bien.
public record NotificacionMensajeDTO(
        String idEvento,
        String destinatario,   // no se usa: el contacto lo resuelve notificaciones-service
        Long donanteId,        // id de la PersonaHumana
        Long entidadId,        // no se usa en este evento
        String mensaje,
        String medio           // null: se usa el medio preferido del donante
) {}