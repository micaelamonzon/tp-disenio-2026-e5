package ar.edu.utn.frba.ddsi.donaciones.security;

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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

// Llamadas entre servicios
// El servicio que llama manda "X-Api-Token: <su token>". Si coincide con alguno de
// api.tokens.*, queda autenticado como ROLE_SERVICIO y en el log figura qué servicio fue.
// Los usuarios siguen usando el JWT (Authorization: Bearer), que valida JwtAuthenticationFilter.
public class ApiTokenFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Api-Token";
    public static final String ROL_SERVICIO = "SERVICIO";

    private static final Logger log = LoggerFactory.getLogger(ApiTokenFilter.class);

    private final Map<String, String> tokensPorServicio;

    public ApiTokenFilter(ApiTokensProperties properties) {
        this.tokensPorServicio = properties.tokens();
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
        String servicio = buscarServicio(token);
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

    private String buscarServicio(String token) {
        byte[] recibido = token.getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entrada : tokensPorServicio.entrySet()) {
            // Comparación en tiempo constante: no revela cuántos caracteres coinciden
            if (MessageDigest.isEqual(recibido, entrada.getValue().getBytes(StandardCharsets.UTF_8))) {
                return entrada.getKey();
            }
        }
        return null;
    }
}
