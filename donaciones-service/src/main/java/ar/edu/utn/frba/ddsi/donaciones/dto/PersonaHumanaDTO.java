package ar.edu.utn.frba.ddsi.donaciones.dto;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;

import java.util.ArrayList;
import java.util.List;

public record PersonaHumanaDTO(
        Long id,
        String nombre,
        String apellido,
        Integer DNI,
        String genero,
        Integer edad,
        String direccion,
        List<DonacionSinSegmentarDTO> donaciones,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
) {
}
