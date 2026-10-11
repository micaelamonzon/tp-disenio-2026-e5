package ar.edu.utn.frba.ddsi.donaciones.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

// Solo LEE tokens: los emite autenticacion-service. Verifica la firma con el
// mismo jwt.secret (HS256) y el vencimiento, sin llamar a autenticación.
@Component
public class JwtUtil {

    // Mismos nombres de claims que JwtService de autenticacion-service
    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_NOMBRE = "nombre";
    public static final String CLAIM_ID_DONANTE = "idDonante";
    public static final String CLAIM_ID_ENTIDAD = "idEntidad";
    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_REFRESH = "refresh";

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Lanza JwtException si la firma no coincide, el token venció o está mal formado
    public Claims parsearClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
