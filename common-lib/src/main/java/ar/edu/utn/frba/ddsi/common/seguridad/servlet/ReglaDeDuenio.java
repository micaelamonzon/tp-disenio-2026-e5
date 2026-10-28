package ar.edu.utn.frba.ddsi.common.seguridad.servlet;

import ar.edu.utn.frba.ddsi.common.seguridad.UsuarioAutenticado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

// "Cada uno accede solo a lo suyo": compara el id de la URL con el id que viene en el token.
// Ej: GET /donaciones/humano/{idDonante} solo si el token es de un DONANTE_HUMANO con ese
// idDonante. ADMIN puede acceder a todo. Así no alcanza con cambiar el número en la URL.
public final class ReglaDeDuenio {

    private static final Logger log = LoggerFactory.getLogger(ReglaDeDuenio.class);
    private static final String ADMIN = "ADMIN";
    private static final String SERVICIO = ApiTokenFilter.ROL_SERVICIO;

    private ReglaDeDuenio() {
    }

    // {idDonante} de la URL tiene que coincidir con el claim idDonante (y el rol con el tipo de donante)
    public static AuthorizationManager<RequestAuthorizationContext> donante(String rol) {
        return duenioOAdmin(Set.of(rol), "idDonante", UsuarioAutenticado::idDonante);
    }

    // Igual que donante(rol), pero también deja pasar a otro servicio (API token, ROLE_SERVICIO).
    // Ej: notificaciones y logística consultan el contacto de cualquier donante para avisarle.
    public static AuthorizationManager<RequestAuthorizationContext> donanteOServicio(String rol) {
        AuthorizationManager<RequestAuthorizationContext> duenio = donante(rol);
        return (authentication, context) -> {
            Authentication auth = authentication.get();
            if (auth != null && auth.getPrincipal() instanceof UsuarioAutenticado usuario
                    && SERVICIO.equals(usuario.rol())) {
                return new AuthorizationDecision(true);
            }
            return duenio.authorize(authentication, context);
        };
    }

    // Para servicios que guardan el idDonante sin saber si es humano o jurídico (ej: logística).
    // Ojo: el humano 1 y el jurídico 1 son personas distintas con el mismo número.
    public static AuthorizationManager<RequestAuthorizationContext> donanteDeCualquierTipo() {
        return duenioOAdmin(Set.of("DONANTE_HUMANO", "DONANTE_JURIDICO"), "idDonante", UsuarioAutenticado::idDonante);
    }

    // {idEntidad} de la URL tiene que coincidir con el claim idEntidad
    public static AuthorizationManager<RequestAuthorizationContext> entidad() {
        return duenioOAdmin(Set.of("ENTIDAD_BENEFICIARIA"), "idEntidad", UsuarioAutenticado::idEntidad);
    }

    private static AuthorizationManager<RequestAuthorizationContext> duenioOAdmin(
            Set<String> roles, String variableDeUrl, Function<UsuarioAutenticado, Long> idDelToken) {
        return (authentication, context) -> {
            Authentication auth = authentication.get();
            if (auth == null || !(auth.getPrincipal() instanceof UsuarioAutenticado usuario)) {
                return new AuthorizationDecision(false); // anónimo -> 401
            }
            if (ADMIN.equals(usuario.rol())) {
                return new AuthorizationDecision(true);
            }
            String idDeUrl = context.getVariables().get(variableDeUrl);
            boolean esDuenio = roles.contains(usuario.rol())
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
