package ar.edu.utn.frba.ddsi.donaciones.dto;
import java.time.LocalDateTime;
import java.util.List;


public record PersonaDonanteDTO (
        Long idHumano,
        String nombre,
        String apellido,
        Long idJuridico,
        String razonSocial,
        LocalDateTime fechaDeRegistro,
        List<DonacionIncentivosDTO> donaciones
){

}
