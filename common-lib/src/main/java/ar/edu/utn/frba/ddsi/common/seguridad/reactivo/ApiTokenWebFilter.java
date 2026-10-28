package ar.edu.utn.frba.ddsi.common.seguridad.reactivo;

import ar.edu.utn.frba.ddsi.common.seguridad.ApiTokensProperties;
import ar.edu.utn.frba.ddsi.common.seguridad.UsuarioAutenticado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

// Versión reactiva
public class ApiTokenWebFilter implements WebFilter {

    public static final String HEADER = "X-Api-Token";
    public static final String ROL_SERVICIO = "SERVICIO";

    private static final Logger log = LoggerFactory.getLogger(ApiTokenWebFilter.class);

    private final ApiTokensProperties apiTokens;

    public ApiTokenWebFilter(ApiTokensProperties apiTokens) {
        this.apiTokens = apiTokens;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (token == null) {
            return chain.filter(exchange);
        }

        String peticion = exchange.getRequest().getMethod() + " " + exchange.getRequest().getPath();
        String servicio = apiTokens.buscarServicio(token);
        if (servicio == null) {
            log.warn("{} -> API token desconocido", peticion);
            return RespuestaNoAutorizada.enviar(exchange, "API token inválido");
        }

        UsuarioAutenticado llamador = new UsuarioAutenticado("servicio:" + servicio, ROL_SERVICIO, servicio, null, null);
        var autenticacion = new UsernamePasswordAuthenticationToken(
                llamador, null, List.of(new SimpleGrantedAuthority("ROLE_" + ROL_SERVICIO)));
        log.debug("{} -> servicio {}", peticion, servicio);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(autenticacion));
    }
}
