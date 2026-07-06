package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PersonaDonanteDTO (
        Long id,
        Long idHumano,
        String nombre,
        String apellido,
        Long idJuridico,
        String razonSocial,
        List<DonacionSinSegmentarDTO> donaciones,
        LocalDateTime fechaDeRegistro,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
){}
