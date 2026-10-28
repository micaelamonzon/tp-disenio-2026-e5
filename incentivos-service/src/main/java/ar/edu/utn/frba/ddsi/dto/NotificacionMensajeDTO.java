package ar.edu.utn.frba.ddsi.dto;

// Espejo del DTO que consume notificaciones-service desde la cola.
// Los nombres de los campos tienen que coincidir para que el JSON se lea bien.
public record NotificacionMensajeDTO(
        String idEvento,
        String destinatario,   // no se usa: el contacto lo resuelve notificaciones-service
        Long donanteId,        // id del donante en donaciones-service
        Long entidadId,        // no se usa desde incentivos
        String mensaje,
        String medio,           // null: se usa el medio preferido del donante
        String tipoDonante     // HUMANO o JURIDICO, para que notificaciones busque en la tabla correcta
) {}