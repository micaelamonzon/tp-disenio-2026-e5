package ar.edu.utn.frba.ddsi.notificaciones.dto;

// Versión mínima del DTO que devuelve donaciones-service en
// GET /servicioDeDonaciones/donante/humano/{id}. Jackson ignora los campos
// del JSON que no estén mapeados acá (DNI, direccion, donaciones, etc.)
public record PersonaHumanaDTO(
        String nombre,
        String apellido,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
) {}