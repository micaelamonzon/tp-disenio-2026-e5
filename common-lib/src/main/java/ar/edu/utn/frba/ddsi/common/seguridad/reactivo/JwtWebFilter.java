package ar.edu.utn.frba.ddsi.common.seguridad.reactivo;

import ar.edu.utn.frba.ddsi.common.seguridad.JwtUtil;
import ar.edu.utn.frba.ddsi.common.seguridad.UsuarioAutenticado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

// Versión reactiva (WebFlux) de servlet.JwtAuthenticationFilter: misma lógica.
// Lee "Authorization: Bearer <token>", lo valida y deja al usuario en el contexto de seguridad
// con la authority ROLE_<rol>.
// - Sin token: sigue como anónimo (los endpoints públicos lo aceptan, el resto da 401).
// - Token inválido o vencido: 401 directo, así el front pide uno nuevo con el refresh token.
public class JwtWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtWebFilter.class);

    private final JwtUtil jwtUtil;

    public JwtWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String peticion = exchange.getRequest().getMethod() + " " + exchange.getRequest().getPath();
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("{} -> sin token", peticion);
            return chain.filter(exchange);
        }

        UsuarioAutenticado usuario;
        try {
            Claims claims = jwtUtil.parsearClaims(header.substring(7));

            // Un refresh token solo sirve para pedirle un access token nuevo a autenticación
            if (JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
                throw new JwtException("Se usó un refresh token como access token");
            }

            usuario = new UsuarioAutenticado(
                    claims.getSubject(),
                    claims.get(JwtUtil.CLAIM_ROL, String.class),
                    claims.get(JwtUtil.CLAIM_NOMBRE, String.class),
                    claims.get(JwtUtil.CLAIM_ID_DONANTE, Long.class),
                    claims.get(JwtUtil.CLAIM_ID_ENTIDAD, Long.class));
        } catch (ExpiredJwtException e) {
            log.debug("{} -> token vencido", peticion);
            return RespuestaNoAutorizada.enviar(exchange, "Token vencido");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("{} -> token inválido: {}", peticion, e.getMessage());
            return RespuestaNoAutorizada.enviar(exchange, "Token inválido");
        }

        var authorities = usuario.rol() != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol()))
                : List.<SimpleGrantedAuthority>of();
        var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
        log.debug("{} -> {} ({}, idDonante={}, idEntidad={})", peticion,
                usuario.email(), usuario.rol(), usuario.idDonante(), usuario.idEntidad());

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(autenticacion));
    }
}
