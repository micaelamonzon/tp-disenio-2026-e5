package ar.edu.utn.frba.ddsi.dtos.donaciones;

import lombok.Data;

@Data
public class RegistroEntidadBeneficiariaDTO {
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String password;
    private RepresentanteDTO representante;
}
