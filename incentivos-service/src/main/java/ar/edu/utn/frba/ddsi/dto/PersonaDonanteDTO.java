package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.categorias.NombreDeCategoria;

import java.util.List;

public record PersonaDonanteDTO (
        Long id,
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
        NombreDeCategoria categoria
){}
