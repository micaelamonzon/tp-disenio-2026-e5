package ar.edu.utn.frba.ddsi.donaciones.dto;

//DTO EXCLUSIVAMENTE PARA QUE LO USEMOS CON LOGISTICA !!!
public record DonacionDTO(
        Integer codigo,
        String estado,
        Double pesoKg,
        Double volumenM3,
        Long entidadBeneficiariaId,
        Long donanteId,
        String direccionEntidad,
        Double latitudEntidad,
        Double longitudEntidad,
        String subcategoria
) {
}
