package ar.edu.utn.frba.ddsi.dto;

// Representa el mensaje que se publica en la cola para que
// el servicio de notificaciones lo procese
public record NotificacionMensajeDTO(
        String idEvento,
        String destinatario,     // contacto ya resuelto (entidad, admin)
        Long donanteId,          // se resuelve al consumir (donante)
        Long entidadId,          // identifica a la entidad destinataria, para listar sus notificaciones
        String mensaje,
        String medio,
        String tipoDonante       // HUMANO o JURIDICO (null = HUMANO)
) {}