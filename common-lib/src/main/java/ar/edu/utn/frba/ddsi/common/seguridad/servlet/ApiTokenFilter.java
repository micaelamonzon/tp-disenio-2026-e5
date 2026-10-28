package ar.edu.utn.frba.ddsi.common.seguridad.servlet;

import ar.edu.utn.frba.ddsi.common.seguridad.ApiTokensProperties;
import ar.edu.utn.frba.ddsi.common.seguridad.UsuarioAutenticado;
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

// Llamadas entre servicios
public class ApiTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Api-Token";
    public static final String ROL_SERVICIO = "SERVICIO";

    private static final Logger log = LoggerFactory.getLogger(ApiTokenFilter.class);

    private final ApiTokensProperties apiTokens;

    public ApiTokenFilter(ApiTokensProperties apiTokens) {
        this.apiTokens = apiTokens;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HEADER);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String peticion = request.getMethod() + " " + request.getRequestURI();
        String servicio = apiTokens.buscarServicio(token);
        if (servicio == null) {
            log.warn("{} -> API token desconocido", peticion);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"API token inválido\"}");
            return;
        }

        UsuarioAutenticado llamador = new UsuarioAutenticado("servicio:" + servicio, ROL_SERVICIO, servicio, null, null);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                llamador, null, List.of(new SimpleGrantedAuthority("ROLE_" + ROL_SERVICIO))));
        log.debug("{} -> servicio {}", peticion, servicio);

        filterChain.doFilter(request, response);
    }
}
