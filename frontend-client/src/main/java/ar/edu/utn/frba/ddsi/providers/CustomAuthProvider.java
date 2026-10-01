package ar.edu.utn.frba.ddsi.providers;

import ar.edu.utn.frba.ddsi.dtos.AuthResponseDTO;
import ar.edu.utn.frba.ddsi.dtos.Permiso;
import ar.edu.utn.frba.ddsi.dtos.Rol;
import ar.edu.utn.frba.ddsi.services.AuthApiService;
import ar.edu.utn.frba.ddsi.utils.JwtClaimsReader;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthProvider.class);
    private final AuthApiService externalAuthService;
    private final JwtClaimsReader jwtClaimsReader;

    public CustomAuthProvider(AuthApiService externalAuthService, JwtClaimsReader jwtClaimsReader) {
        this.externalAuthService = externalAuthService;
        this.jwtClaimsReader = jwtClaimsReader;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // Llamada a servicio externo para obtener tokens
            AuthResponseDTO authResponse = externalAuthService.login(username, password);

            if (authResponse == null) {
                throw new BadCredentialsException("Usuario o contraseña inválidos");
            }

            log.info("Usuario logeado! Configurando variables de sesión");
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            request.getSession().setAttribute("accessToken", authResponse.getAccessToken());
            request.getSession().setAttribute("refreshToken", authResponse.getRefreshToken());
            request.getSession().setAttribute("username", username);

            log.info("Leyendo rol y permisos del access token (sin request adicional)");
            Claims claims = jwtClaimsReader.leerClaims(authResponse.getAccessToken());

            Rol rol = Rol.valueOf(claims.get(JwtClaimsReader.CLAIM_ROL, String.class));
            List<Permiso> permisos = ((List<?>) claims.get(JwtClaimsReader.CLAIM_PERMISOS, List.class)).stream()
                    .map(String::valueOf)
                    .map(Permiso::valueOf)
                    .collect(Collectors.toList());

            log.info("Cargando roles y permisos del usuario en sesión");
            request.getSession().setAttribute("rol", rol);
            request.getSession().setAttribute("permisos", permisos);

            List<GrantedAuthority> authorities = new ArrayList<>();
            permisos.forEach(permiso -> authorities.add(new SimpleGrantedAuthority(permiso.name())));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.name()));

            return new UsernamePasswordAuthenticationToken(username, password, authorities);

        } catch (RuntimeException e) {
            throw new BadCredentialsException("Error en el sistema de autenticación: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
