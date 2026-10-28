package ar.edu.utn.frba.ddsi.dtos.entidades;

public record EntidadPublicaDTO(
        Long id,
        String razonSocial,
        String direccion,
        Double latitud,
        Double longitud
) {
}
