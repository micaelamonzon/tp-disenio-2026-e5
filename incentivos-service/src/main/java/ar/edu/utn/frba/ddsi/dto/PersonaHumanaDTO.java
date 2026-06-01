package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record PersonaHumanaDTO (
        Long id,
        String nombre,
        String apellido,
        Integer DNI,
        String genero,
        Integer edad,
        String direccion,
        List<DonacionSinSegmentarDTO> donaciones

){
}
