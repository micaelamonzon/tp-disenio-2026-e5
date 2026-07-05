package ar.edu.utn.frba.ddsi.dto;

public record DonacionDTO(
        Integer codigo,
        String estado,
        Double pesoKg,
        Double volumenM3
) {
}
