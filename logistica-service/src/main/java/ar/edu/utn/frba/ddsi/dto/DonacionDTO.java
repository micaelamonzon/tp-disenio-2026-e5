package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DonacionDTO(
        Integer codigo,
        String estado,
        Double pesoKg,
        Double volumenM3,
        String direccionEntidad,
        double latitudEntidad,
        double longitudEntidad
) {
}
