package ar.edu.utn.frba.ddsi.notificaciones.dto;

// Espejo del DTO que publica logistica-service en la cola.
// Los campos deben coincidir para que el JSON se deserialice correctamente.
public record NotificacionMensajeDTO(
        String destinatario,
        String mensaje,
        String medio
) {}