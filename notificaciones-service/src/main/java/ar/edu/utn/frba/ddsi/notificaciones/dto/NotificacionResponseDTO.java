package ar.edu.utn.frba.ddsi.notificaciones.dto;

import java.time.LocalDateTime;

// Lo que devolvemos al front cuando pide la lista de notificaciones
// No devolvemos la entidad Notificacion directamente para no exponer
// el modelo interno (relación con Donante, medio como objeto, etc)
public record NotificacionResponseDTO(
        Long id,
        String mensaje,
        String medio,       // EMAIL, SMS o WHATSAPP
        String estado,      // PENDIENTE, ENVIADA o FALLIDA
        LocalDateTime fechaEnvio
) {}