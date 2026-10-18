package ar.edu.utn.frba.ddsi.donaciones.dto;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record PersonaHumanaDTO(
        Long id,
        String nombre,
        String apellido,
        Integer edad,
        Integer DNI,
        String genero,
        LocalDate fechaDeNacimiento,
        List<DonacionSinSegmentarDTO> donaciones,
        List<MedioDeNotificacionDTO> mediosDeNotificacion,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
) {
}
