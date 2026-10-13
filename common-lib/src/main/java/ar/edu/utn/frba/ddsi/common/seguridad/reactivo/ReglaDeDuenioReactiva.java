package ar.edu.utn.frba.ddsi.common.seguridad.reactivo;

import ar.edu.utn.frba.ddsi.common.seguridad.UsuarioAutenticado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

// Versión reactiva (WebFlux) de servlet.ReglaDeDuenio: misma lógica.
// "Cada uno accede solo a lo suyo": compara el id de la URL con el id que viene en el token.
// ADMIN puede acceder a todo. Así no alcanza con cambiar el número en la URL.
public final class ReglaDeDuenioReactiva {

    private static final Logger log = LoggerFactory.getLogger(ReglaDeDuenioReactiva.class);
    private static final String ADMIN = "ADMIN";

    private ReglaDeDuenioReactiva() {
    }

    // {idDonante} de la URL tiene que coincidir con el claim idDonante (donante humano o jurídico)
    public static ReactiveAuthorizationManager<AuthorizationContext> donanteDeCualquierTipo() {
        return duenioOAdmin(Set.of("DONANTE_HUMANO", "DONANTE_JURIDICO"), "idDonante", UsuarioAutenticado::idDonante);
    }

    // {idEntidad} de la URL tiene que coincidir con el claim idEntidad
    public static ReactiveAuthorizationManager<AuthorizationContext> entidad() {
        return duenioOAdmin(Set.of("ENTIDAD_BENEFICIARIA"), "idEntidad", UsuarioAutenticado::idEntidad);
    }

    private static ReactiveAuthorizationManager<AuthorizationContext> duenioOAdmin(
            Set<String> roles, String variableDeUrl, Function<UsuarioAutenticado, Long> idDelToken) {
        return (authentication, context) -> authentication
                .map(auth -> {
                    if (!(auth.getPrincipal() instanceof UsuarioAutenticado usuario)) {
                        return (AuthorizationResult) new AuthorizationDecision(false); // anónimo -> 401
                    }
                    if (ADMIN.equals(usuario.rol())) {
                        return (AuthorizationResult) new AuthorizationDecision(true);
                    }
                    Object idDeUrl = context.getVariables().get(variableDeUrl);
                    boolean esDuenio = roles.contains(usuario.rol())
                            && idDeUrl != null
                            && Objects.equals(String.valueOf(idDelToken.apply(usuario)), String.valueOf(idDeUrl));
                    if (!esDuenio) {
                        log.warn("{} ({} {}) intentó acceder a {} de otro: {}", usuario.email(), usuario.rol(),
                                idDelToken.apply(usuario), variableDeUrl, idDeUrl);
                    }
                    return (AuthorizationResult) new AuthorizationDecision(esDuenio);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
