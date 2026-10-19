package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;

public record EntidadPublicaDTO(
        Long id, String razonSocial, String direccion,
        Double latitud, Double longitud
) {
}
