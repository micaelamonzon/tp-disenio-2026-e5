package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.logistica.SeguimientoCamionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// Provee los camiones en camino hacia una entidad beneficiaria.
// TODO: reemplazar los datos de prueba por la llamada a logistica-service
// cuando esté disponible el endpoint con las ubicaciones de los camiones.
@Service
public class SeguimientoService {

    public List<SeguimientoCamionDTO> obtenerCamionesEnCaminoAEntidad(Long entidadId) {
        LocalDateTime ahora = LocalDateTime.now();
        return List.of(
                new SeguimientoCamionDTO(
                        "AB 123 CD", -34.6037, -58.3816, ahora.minusMinutes(2),
                        "50 paquetes de fideos secos",
                        "Av. Rivadavia 4500, CABA", -34.6158, -58.4290,
                        "En traslado"),
                new SeguimientoCamionDTO(
                        "AE 456 FG", -34.5875, -58.4106, ahora.minusMinutes(7),
                        "30 frazadas",
                        "Av. Rivadavia 4500, CABA", -34.6158, -58.4290,
                        "En traslado")
        );
    }
}