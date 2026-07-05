package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.categorias.NombreDeCategoria;

import java.time.LocalDateTime;
import java.util.List;

public record PersonaDonanteDTO (
        Long id,
        Long idHumano,
        String nombre,
        String apellido,
        Long idJuridico,
        List<DonacionSinSegmentarDTO> donaciones,
        String razonSocial,
        LocalDateTime fechaDeRegistro,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
){}
