package ar.edu.utn.frba.ddsi.dtos.donaciones;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RegistroPersonaHumanaDTO {
    private String nombre;
    private String apellido;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaDeNacimiento;
    private Integer DNI;
    private String genero;
    private String direccion;
    private String email;
    private String password;
}
