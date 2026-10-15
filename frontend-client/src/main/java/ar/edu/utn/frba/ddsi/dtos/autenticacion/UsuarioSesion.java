package ar.edu.utn.frba.ddsi.dtos.autenticacion;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;

import java.io.Serializable;

// Usuario logueado, guardado en la sesión (atributo "usuario") al iniciar sesión.
// Las plantillas leen nombre, rol (texto) e iniciales; los controllers, codigoRol e idEnDominio.
// Serializable porque vive dentro de la HttpSession.
public record UsuarioSesion(String email, String nombre, Rol codigoRol, Long idEnDominio)
        implements Serializable {

    // Texto del rol para mostrar (las plantillas usan session.usuario.rol)
    public String getRol() {
        return codigoRol.getEtiqueta();
    }

    // Para el avatar (session.usuario.iniciales)
    public String getIniciales() {
        if (nombre == null || nombre.isBlank()) {
            return "";
        }
        java.util.Set<String> ignoradas = java.util.Set.of("de", "del", "la", "las", "los", "el", "y");
        StringBuilder sb = new StringBuilder();
        for (String p : nombre.trim().split("\\s+")) {
            if (!ignoradas.contains(p.toLowerCase())) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() == 2) break;
            }
        }
        return sb.toString();
    }



    public boolean esDonanteHumano() {
        return codigoRol == Rol.DONANTE_HUMANO;
    }

    public TipoDeDonante tipoDeDonante() {
        return codigoRol.tipoDeDonante();
    }
}
