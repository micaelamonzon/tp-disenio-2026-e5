package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.CategoriaDeDonante;

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
        List<MisionDTO> misiones,
        CategoriaDeDonante categoria
){

}
