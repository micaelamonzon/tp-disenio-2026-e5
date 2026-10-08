package ar.edu.utn.frba.ddsi.providers;

import ar.edu.utn.frba.ddsi.dtos.AuthResponseDTO;
import ar.edu.utn.frba.ddsi.dtos.Rol;
import ar.edu.utn.frba.ddsi.dtos.UsuarioSesion;
import ar.edu.utn.frba.ddsi.services.AuthApiService;
import ar.edu.utn.frba.ddsi.utils.JwtClaimsReader;
import ar.edu.utn.frba.ddsi.utils.UsuarioActual;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

// Login del navegador: le pregunta a autenticacion-service y, si las credenciales son válidas,
// guarda en la sesión del front (cookie JSESSIONID) los tokens y los datos del usuario.
// El navegador nunca recibe los tokens.
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthProvider.class);
    private final AuthApiService authApiService;
    private final JwtClaimsReader jwtClaimsReader;

    public CustomAuthProvider(AuthApiService authApiService, JwtClaimsReader jwtClaimsReader) {
        this.authApiService = authApiService;
        this.jwtClaimsReader = jwtClaimsReader;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        AuthResponseDTO tokens;
        try {
            tokens = authApiService.login(email, password);
        } catch (RuntimeException e) {
            // Servicio caído u otro error: se informa distinto que unas credenciales incorrectas
            log.error("No se pudo contactar al servicio de autenticación", e);
            throw new AuthenticationServiceException("El servicio de autenticación no está disponible", e);
        }
        if (tokens == null) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        UsuarioSesion usuario;
        try {
            Claims claims = jwtClaimsReader.leerClaims(tokens.getAccessToken());
            Rol rol = Rol.valueOf(claims.get(JwtClaimsReader.CLAIM_ROL, String.class));
            usuario = new UsuarioSesion(
                    claims.getSubject(),
                    claims.get(JwtClaimsReader.CLAIM_NOMBRE, String.class),
                    rol,
                    leerIdEnDominio(claims, rol));
        } catch (RuntimeException e) {
            // Firma inválida (jwt.secret distinto al del servicio) o un rol desconocido
            log.error("No se pudo leer el access token", e);
            throw new AuthenticationServiceException("No se pudo procesar el token de autenticación", e);
        }

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        session.setAttribute("accessToken", tokens.getAccessToken());   // los usa WebApiCallerService
        session.setAttribute("refreshToken", tokens.getRefreshToken());
        session.setAttribute(UsuarioActual.ATRIBUTO_SESION, usuario);

        log.info("Inicio de sesión de {} con rol {}", usuario.email(), usuario.codigoRol());
        // Sin contraseña: ya no hace falta y no conviene guardarla en la sesión
        return new UsernamePasswordAuthenticationToken(
                usuario.email(), null, List.of(new SimpleGrantedAuthority("ROLE_" + usuario.codigoRol().name())));
    }

    // El token publica el id del dominio con un nombre que depende del rol
    private Long leerIdEnDominio(Claims claims, Rol rol) {
        String claim = switch (rol) {
            case DONANTE_HUMANO, DONANTE_JURIDICO -> JwtClaimsReader.CLAIM_ID_DONANTE;
            case ENTIDAD_BENEFICIARIA -> JwtClaimsReader.CLAIM_ID_ENTIDAD;
            case ADMIN -> null;
        };
        Object valor = claim != null ? claims.get(claim) : null;
        // JJWT devuelve los números como Integer o Long según su tamaño
        return valor instanceof Number n ? n.longValue() : null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
