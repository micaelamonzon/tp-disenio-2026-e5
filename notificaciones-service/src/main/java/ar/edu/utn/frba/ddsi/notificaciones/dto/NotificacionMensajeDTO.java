package ar.edu.utn.frba.ddsi.notificaciones.dto;

// Espejo del DTO que publica logistica-service en la cola.
// Los campos deben coincidir para que el JSON se deserialice correctamente.
public record NotificacionMensajeDTO(
        String idEvento,
        String destinatario,     // contacto ya resuelto (entidad, admin)
        Long donanteId,          // se resuelve al consumir (donante)
        Long entidadId,          // identifica a la entidad beneficiaria destinataria
        String mensaje,
        String medio,
        String tipoDonante       // HUMANO o JURIDICO; si viene null se toma HUMANO
) {}