package ar.edu.utn.frba.ddsi.donaciones.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Objects;
import java.util.function.Function;

// "Cada uno accede solo a lo suyo": compara el id de la URL con el id que viene en el token.
// Ej: GET /donaciones/humano/{idDonante} solo si el token es de un DONANTE_HUMANO con ese
// idDonante. ADMIN puede acceder a todo. Así no alcanza con cambiar el número en la URL.
public final class ReglaDeDuenio {

    private static final Logger log = LoggerFactory.getLogger(ReglaDeDuenio.class);
    private static final String ADMIN = "ADMIN";

    private ReglaDeDuenio() {
    }

    // {idDonante} de la URL tiene que coincidir con el claim idDonante (y el rol con el tipo de donante)
    public static AuthorizationManager<RequestAuthorizationContext> donante(String rol) {
        return duenioOAdmin(rol, "idDonante", UsuarioAutenticado::idDonante);
    }

    // {idEntidad} de la URL tiene que coincidir con el claim idEntidad
    public static AuthorizationManager<RequestAuthorizationContext> entidad() {
        return duenioOAdmin("ENTIDAD_BENEFICIARIA", "idEntidad", UsuarioAutenticado::idEntidad);
    }

    private static AuthorizationManager<RequestAuthorizationContext> duenioOAdmin(
            String rol, String variableDeUrl, Function<UsuarioAutenticado, Long> idDelToken) {
        return (authentication, context) -> {
            Authentication auth = authentication.get();
            if (auth == null || !(auth.getPrincipal() instanceof UsuarioAutenticado usuario)) {
                return new AuthorizationDecision(false); // anónimo -> 401
            }
            if (ADMIN.equals(usuario.rol())) {
                return new AuthorizationDecision(true);
            }
            String idDeUrl = context.getVariables().get(variableDeUrl);
            boolean esDuenio = rol.equals(usuario.rol())
                    && idDeUrl != null
                    && Objects.equals(String.valueOf(idDelToken.apply(usuario)), idDeUrl);
            if (!esDuenio) {
                log.warn("{} ({} {}) intentó acceder a {} de otro: {}", usuario.email(), usuario.rol(),
                        idDelToken.apply(usuario), variableDeUrl, idDeUrl);
            }
            return new AuthorizationDecision(esDuenio);
        };
    }
}
