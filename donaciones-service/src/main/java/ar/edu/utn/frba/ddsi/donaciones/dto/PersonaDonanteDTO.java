package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.util.List;

public record PersonaDonanteDTO (
        Long idHumano,
        String nombre,
        String apellido,
        Integer DNI,
        String genero,
        Integer edad,
        String direccion,
        Long idJuridico,
        List<DonacionSinSegmentarDTO> donaciones,
        String cuit,
        String razonSocial,
        List<MisionDTO> misiones
){

}
