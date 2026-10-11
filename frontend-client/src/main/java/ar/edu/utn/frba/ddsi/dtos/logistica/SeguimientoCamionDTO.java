package ar.edu.utn.frba.ddsi.dtos.logistica;

import java.time.LocalDateTime;

// Datos de un camión en camino hacia la entidad, para la pantalla de seguimiento (req. 14).
// Por ahora se arma con datos de prueba en SeguimientoService; cuando logística exponga
// las ubicaciones, se completa con su respuesta.
public record SeguimientoCamionDTO(
        String patente,
        Double latitud,
        Double longitud,
        LocalDateTime ultimaActualizacion,
        String descripcionEntrega,
        String destinoDireccion,
        Double destinoLatitud,
        Double destinoLongitud,
        String estado
) {}