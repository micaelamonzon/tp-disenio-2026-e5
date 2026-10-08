package ar.edu.utn.frba.ddsi.dtos;

public record CrearUsuarioDTO(String email, String password, String rol, Long idEnDominio, String nombre) {
}
