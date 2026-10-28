package ar.edu.utn.frba.ddsi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

// Respuesta de POST /api/usuarios. Nunca incluye el hash de la contraseña.
// passwordTemporal solo aparece cuando se generó una (y se devuelve una única vez).
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UsuarioDTO(Long id, String email, String rol, Long idEnDominio, String nombre,
                         String passwordTemporal) {
}
