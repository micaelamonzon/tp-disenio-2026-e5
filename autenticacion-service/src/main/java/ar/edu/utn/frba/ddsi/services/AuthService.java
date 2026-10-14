package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.TokensDTO;
import ar.edu.utn.frba.ddsi.dtos.UsuarioDTO;
import ar.edu.utn.frba.ddsi.models.NotificacionesClient;
import ar.edu.utn.frba.ddsi.models.Usuario;
import ar.edu.utn.frba.ddsi.repositories.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.internal.logging.InternalLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private InternalLogger log;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokensDTO login(String email, String password) {
        // Mismo error si no existe el email o si la contraseña no coincide
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);

        return new TokensDTO(jwtService.generarAccessToken(usuario), jwtService.generarRefreshToken(usuario));
    }

    public TokensDTO refresh(String refreshToken) {
        String email;
        try {
            email = jwtService.validarRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredencialesInvalidasException();
        }
        // Se vuelve a leer el usuario: si cambió su rol o lo desactivaron, se aplica ahora
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .orElseThrow(CredencialesInvalidasException::new);

        // Access nuevo; el refresh se mantiene (vence a los 7 días del login)
        return new TokensDTO(jwtService.generarAccessToken(usuario), refreshToken);
    }

    public static class CredencialesInvalidasException extends RuntimeException {
        public CredencialesInvalidasException() {
            super("Credenciales inválidas");
        }
    }

    @Async
    public void solicitarRecuperacion(String email) {
        try {
            usuarioRepository.findByEmail(email)
                    .filter(Usuario::isActivo)
                    .ifPresent(usuario -> {
                        String token = jwtService.generarTokenRecuperacion(usuario);
                        String link = "http://localhost:8084/acceso/nueva-password?token=" + token;
                        NotificacionesClient.enviarEmail(
                                usuario.getEmail(),
                                "Hola, solicitaste restablecer tu contraseña. Elegí una nueva desde este enlace: "
                                        + link + " (expira en 15 minutos).");
                    });
        } catch (Exception e) {
            log.error("No se pudo enviar el mail de recuperación: {}", e.getMessage());
        }
    }
    public void actualizarPasswordConJwtToken(String token, String nuevaPassword) {
        String email;
        try {
            email = jwtService.validarTokenRecuperacion(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("El enlace de recuperación es inválido o ha expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nuevaPassword == null || nuevaPassword.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }
}
