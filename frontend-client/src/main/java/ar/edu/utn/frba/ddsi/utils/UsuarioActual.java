package ar.edu.utn.frba.ddsi.utils;

import ar.edu.utn.frba.ddsi.dtos.UsuarioSesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// Único lugar donde se resuelve "¿quién es el usuario logueado?".
// Lee el UsuarioSesion que CustomAuthProvider guardó en la sesión al iniciar sesión.
@Component
public class UsuarioActual {

    public static final String ATRIBUTO_SESION = "usuario";

    public UsuarioSesion obtener() {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession(false);
        Object usuario = session != null ? session.getAttribute(ATRIBUTO_SESION) : null;
        if (!(usuario instanceof UsuarioSesion u)) {
            // No debería pasar: SecurityConfig exige login para las pantallas que lo usan
            throw new IllegalStateException("No hay un usuario logueado en la sesión");
        }
        return u;
    }

    // Id del donante (humano o jurídico) o de la entidad en donaciones-service
    public Long idEnDominio() {
        return obtener().idEnDominio();
    }
}
