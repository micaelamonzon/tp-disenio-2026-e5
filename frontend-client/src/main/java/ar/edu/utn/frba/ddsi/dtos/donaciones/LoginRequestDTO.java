package ar.edu.utn.frba.ddsi.dtos.donaciones;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
