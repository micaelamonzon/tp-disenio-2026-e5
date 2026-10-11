package ar.edu.utn.frba.ddsi.donaciones.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Lee "Authorization: Bearer <token>", lo valida y deja al usuario en el SecurityContext
// con la authority ROLE_<rol>. Qué rol puede usar cada endpoint lo decide SecurityConfig.

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String peticion = request.getMethod() + " " + request.getRequestURI();
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("{} -> sin token", peticion);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtUtil.parsearClaims(header.substring(7));

            // Un refresh token solo sirve para pedirle un access token nuevo a autenticación
            if (JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
                throw new JwtException("Se usó un refresh token como access token");
            }

            UsuarioAutenticado usuario = new UsuarioAutenticado(
                    claims.getSubject(),
                    claims.get(JwtUtil.CLAIM_ROL, String.class),
                    claims.get(JwtUtil.CLAIM_NOMBRE, String.class),
                    claims.get(JwtUtil.CLAIM_ID_DONANTE, Long.class),
                    claims.get(JwtUtil.CLAIM_ID_ENTIDAD, Long.class));

            var authorities = usuario.rol() != null
                    ? List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol()))
                    : List.<SimpleGrantedAuthority>of();
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(usuario, null, authorities));

            log.debug("{} -> {} ({}, idDonante={}, idEntidad={})", peticion,
                    usuario.email(), usuario.rol(), usuario.idDonante(), usuario.idEntidad());
        } catch (ExpiredJwtException e) {
            log.debug("{} -> token vencido", peticion);
            responder401(response, "Token vencido");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("{} -> token inválido: {}", peticion, e.getMessage());
            responder401(response, "Token inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void responder401(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }
}
