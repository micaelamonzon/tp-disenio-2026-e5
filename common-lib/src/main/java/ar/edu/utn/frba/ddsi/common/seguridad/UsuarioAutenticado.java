package ar.edu.utn.frba.ddsi.common.seguridad;

// Quién hace el request, armado con los claims del token (o con el API token si es un servicio).
// Queda como "principal" en el SecurityContext; se usa para chequear
// que un donante/entidad solo acceda a lo suyo (idDonante / idEntidad).
public record UsuarioAutenticado(
        String email,
        String rol,
        String nombre,
        Long idDonante,
        Long idEntidad
) {
}
