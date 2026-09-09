package ar.edu.utn.frba.ddsi.notificaciones.dto;

// Versión mínima del DTO que devuelve donaciones-service en
// GET /servicioDeDonaciones/humano/{id}. Solo necesitamos el contacto
// predeterminado, así que ignoramos el resto de los campos del JSON
// (nombre, apellido, DNI, donaciones, etc.). Jackson los descarta solo.
public record PersonaHumanaDTO(
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
) {}