package ar.edu.utn.frba.ddsi.donaciones.security;

// Quién hace el request, armado con los claims del token.
// Queda como "principal" en el SecurityContext; se va a usar para chequear
// que un donante/entidad solo acceda a lo suyo (idDonante / idEntidad).
public record UsuarioAutenticado(
        String email,
        String rol,
        String nombre,
        Long idDonante,
        Long idEntidad
) {
}
