package ar.edu.utn.frba.ddsi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

// Verifica la firma del access token emitido por autenticacion-service y lee sus claims
@Component
public class JwtClaimsReader {

    // Mismos nombres que usa JwtService en autenticacion-service
    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_NOMBRE = "nombre";
    public static final String CLAIM_ID_DONANTE = "idDonante";
    public static final String CLAIM_ID_ENTIDAD = "idEntidad";

    private final Key key;

    public JwtClaimsReader(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims leerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
