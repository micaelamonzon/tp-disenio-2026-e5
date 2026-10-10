package ar.edu.utn.frba.ddsi.donaciones.dto;

//DTO EXCLUSIVAMENTE PARA QUE LO USEMOS CON LOGISTICA !!!
public record DonacionDTO(
        Integer codigo,
        String estado,
        Double pesoKg,
        Double volumenM3,
        String direccionEntidad,
        double latitudEntidad,
        double longitudEntidad
) {
}
