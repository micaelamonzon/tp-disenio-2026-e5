package ar.edu.utn.frba.ddsi.dto;

// Representa el mensaje que se publica en la cola para que
// el servicio de notificaciones lo procese
public record NotificacionMensajeDTO(
        String destinatario,
        String mensaje,
        String medio   // EMAIL, SMS o WHATSAPP
) {}