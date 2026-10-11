package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.CrearUsuarioDTO;
import ar.edu.utn.frba.ddsi.dtos.UsuarioDTO;
import ar.edu.utn.frba.ddsi.services.UsuarioService;
import ar.edu.utn.frba.ddsi.services.UsuarioService.DatosInvalidosException;
import ar.edu.utn.frba.ddsi.services.UsuarioService.EmailEnUsoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// TODO proteger con rol SERVICIO/ADMIN
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crear(@RequestBody CrearUsuarioDTO body) {
        return usuarioService.crear(body);
    }

    // El registro lo consulta ANTES de crear el donante/entidad en donaciones,
    // así un email repetido no deja un donante sin usuario
    @GetMapping("/disponible")
    public Map<String, Boolean> emailDisponible(@RequestParam String email) {
        return Map.of("disponible", usuarioService.emailDisponible(email));
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(DatosInvalidosException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(EmailEnUsoException.class)
    public ResponseEntity<Map<String, String>> emailEnUso(EmailEnUsoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}
