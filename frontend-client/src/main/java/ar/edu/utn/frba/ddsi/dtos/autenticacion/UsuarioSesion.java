package ar.edu.utn.frba.ddsi.dtos.autenticacion;

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
        StringBuilder sb = new StringBuilder();
        String texto = (nombre == null || nombre.isBlank()) ? email : nombre;
        for (String parte : texto.trim().split("\\s+")) {
            if (!parte.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(parte.charAt(0)));
            }
        }
        return sb.toString();
    }

    public boolean esDonanteHumano() {
        return codigoRol == Rol.DONANTE_HUMANO;
    }
}
