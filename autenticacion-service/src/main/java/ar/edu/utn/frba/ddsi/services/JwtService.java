package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.models.Rol;
import ar.edu.utn.frba.ddsi.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

// Genera y valida los tokens (firma HS256 con jwt.secret, compartido con el front)
@Service
public class JwtService {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_NOMBRE = "nombre";
    public static final String CLAIM_ID_DONANTE = "idDonante";
    public static final String CLAIM_ID_ENTIDAD = "idEntidad";
    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_REFRESH = "refresh";
    public static final String TYPE_RECUPERACION = "recuperacion";

    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000L;            // 15 minutos
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;  // 7 días
    private static final long RECUPERACION_TOKEN_VALIDITY = 15 * 60 * 1000L;

    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarAccessToken(Usuario usuario) {
        var builder = Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim(CLAIM_ROL, usuario.getRol().name())
                .claim(CLAIM_NOMBRE, usuario.getNombre())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY));

        // idEnDominio se publica con un nombre que dice qué es, según el rol
        String claimId = claimDeId(usuario.getRol());
        if (claimId != null && usuario.getIdEnDominio() != null) {
            builder.claim(claimId, usuario.getIdEnDominio());
        }
        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String generarRefreshToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Valida firma y vencimiento de un refresh token y devuelve el email (sub). */
    public String validarRefreshToken(String refreshToken) {
        Claims claims = parsear(refreshToken);
        // Un access token no sirve para refrescar
        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE))) {
            throw new JwtException("El token no es de tipo refresh");
        }
        return claims.getSubject();
    }

    private Claims parsear(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String claimDeId(Rol rol) {
        return switch (rol) {
            case DONANTE_HUMANO, DONANTE_JURIDICO -> CLAIM_ID_DONANTE;
            case ENTIDAD_BENEFICIARIA -> CLAIM_ID_ENTIDAD;
            case ADMIN -> null;
        };
    }
    public String generarTokenRecuperacion(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim(CLAIM_TYPE, TYPE_RECUPERACION)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RECUPERACION_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validarTokenRecuperacion(String token) {
        Claims claims = parsear(token);
        if (!TYPE_RECUPERACION.equals(claims.get(CLAIM_TYPE))) {
            throw new JwtException("El token no es de tipo recuperación");
        }
        return claims.getSubject();
    }
}
