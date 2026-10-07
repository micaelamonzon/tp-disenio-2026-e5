package ar.edu.utn.frba.ddsi.dtos.donaciones;

import lombok.Data;

@Data
public class RegistroPersonaHumanaDTO {
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer DNI;
    private String genero;
    private String direccion;
    private String email;
    private String password;
}
