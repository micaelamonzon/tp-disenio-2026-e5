package ar.edu.utn.frba.ddsi.donaciones.dto;
import java.util.List;


public record PersonaDonanteDTO (
        Long idHumano,
        String nombre,
        String apellido,
        Long idJuridico,
        List<DonacionSinSegmentarDTO> donaciones,
        String razonSocial,
        MedioDeNotificacionDTO medioDeNotificacionPredeterminado
){

}
