package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.*;
import ar.edu.utn.frba.ddsi.services.AuthService;
import ar.edu.utn.frba.ddsi.services.AuthService.CredencialesInvalidasException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public TokensDTO login(@RequestBody LoginRequestDTO body) {
        if (vacio(body.email()) || vacio(body.password())) {
            throw new DatosIncompletosException("Faltan el email o la contraseña");
        }
        return authService.login(body.email().trim(), body.password());
    }

    @PostMapping("/refresh")
    public TokensDTO refresh(@RequestBody RefreshRequestDTO body) {
        if (vacio(body.refreshToken())) {
            throw new DatosIncompletosException("Falta el refresh token");
        }
        return authService.refresh(body.refreshToken());
    }

    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<MensajeRespuestaDTO> solicitarRecuperacion(@RequestBody RecuperarPasswordRequestDTO body) {
        authService.solicitarRecuperacion(body.email());
        return ResponseEntity.accepted()
                .body(new MensajeRespuestaDTO("Si el correo está registrado, vas a recibir un enlace de recuperación."));
    }

    @PostMapping("/cambiar-con-token")
    public ResponseEntity<MensajeRespuestaDTO> cambiarConToken(@RequestBody CambiarPasswordRequestDTO body) {
        authService.actualizarPasswordConJwtToken(body.token(), body.nuevaPassword());
        return ResponseEntity.ok(new MensajeRespuestaDTO("Contraseña actualizada con éxito"));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> credencialesInvalidas(CredencialesInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(DatosIncompletosException.class)
    public ResponseEntity<Map<String, String>> datosIncompletos(DatosIncompletosException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    static class DatosIncompletosException extends RuntimeException {
        DatosIncompletosException(String mensaje) {
            super(mensaje);
        }
    }
}
