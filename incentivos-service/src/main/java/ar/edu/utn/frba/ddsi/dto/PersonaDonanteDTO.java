package ar.edu.utn.frba.ddsi.dto;

import java.util.List;

public record PersonaDonanteDTO (
        Long idHumana,
        String nombre,
        String apellido,
        Integer DNI,
        String genero,
        Integer edad,
        String direccion,
        List<DonacionSinSegmentarDTO> donacionesHumana,
        Long idJuridica,
        String cuit,
        String razonSocial,
        List<DonacionSinSegmentarDTO> donacionesJuridica
){}
