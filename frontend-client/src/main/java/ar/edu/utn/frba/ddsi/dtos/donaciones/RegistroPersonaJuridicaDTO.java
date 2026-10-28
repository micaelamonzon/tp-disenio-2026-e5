package ar.edu.utn.frba.ddsi.dtos.donaciones;

import lombok.Data;

@Data
public class RegistroPersonaJuridicaDTO {
    private String cuit;
    private String razonSocial;
    private String rubro;
    private String tipo;
    private String email;
    private String password;
}
