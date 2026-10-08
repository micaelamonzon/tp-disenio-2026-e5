package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.TokensDTO;
import ar.edu.utn.frba.ddsi.models.Usuario;
import ar.edu.utn.frba.ddsi.repositories.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
}
