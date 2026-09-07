package ar.edu.utn.frba.ddsi.donaciones.dto;
import java.util.List;


public record PersonaDonanteDTO (
        Long idHumano,
        String nombre,
        String apellido,
        Long idJuridico,
        String razonSocial,
        List<DonacionIncentivosDTO> donaciones
){

}
