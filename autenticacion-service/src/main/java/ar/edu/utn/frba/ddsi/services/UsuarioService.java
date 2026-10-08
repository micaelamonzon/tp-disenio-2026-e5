package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.CrearUsuarioDTO;
import ar.edu.utn.frba.ddsi.dtos.UsuarioDTO;
import ar.edu.utn.frba.ddsi.models.Rol;
import ar.edu.utn.frba.ddsi.models.Usuario;
import ar.edu.utn.frba.ddsi.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

// Alta de usuarios, la usaran el registro web, el admin en el deposito y la importación por CSV
@Service
public class UsuarioService {

    private static final int LARGO_MINIMO_PASSWORD = 8;
    private static final int LARGO_PASSWORD_TEMPORAL = 12;
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDTO crear(CrearUsuarioDTO body) {
        String email = normalizarEmail(body.email());
        Rol rol = validarRol(body.rol());
        if (body.idEnDominio() == null) {
            throw new DatosInvalidosException("Falta idEnDominio (id del donante o de la entidad en donaciones)");
        }
        if (vacio(body.nombre())) {
            throw new DatosInvalidosException("Falta el nombre");
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new EmailEnUsoException(email);
        }

        // Sin contraseña -> se genera una temporal para enviarle al usuario
        String passwordTemporal = null;
        String password = body.password();
        if (vacio(password)) {
            passwordTemporal = generarPasswordTemporal();
            password = passwordTemporal;
        } else if (password.length() < LARGO_MINIMO_PASSWORD) {
            throw new DatosInvalidosException(
                    "La contraseña debe tener al menos " + LARGO_MINIMO_PASSWORD + " caracteres");
        }

        Usuario usuario = new Usuario(email, passwordEncoder.encode(password), rol,
                body.idEnDominio(), body.nombre().trim());
        try {
            usuario = usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            // Dos altas simultáneas con el mismo email: la restricción unique de la base lo frena
            throw new EmailEnUsoException(email);
        }

        return new UsuarioDTO(usuario.getId(), usuario.getEmail(), usuario.getRol().name(),
                usuario.getIdEnDominio(), usuario.getNombre(), passwordTemporal);
    }

    private String normalizarEmail(String email) {
        if (vacio(email) || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new DatosInvalidosException("El email es obligatorio y debe tener un formato válido");
        }
        return email.trim().toLowerCase();
    }

    private Rol validarRol(String rol) {
        if (vacio(rol)) {
            throw new DatosInvalidosException("Falta el rol");
        }
        Rol valor;
        try {
            valor = Rol.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatosInvalidosException(
                    "Rol inválido. Valores posibles: DONANTE_HUMANO, DONANTE_JURIDICO, ENTIDAD_BENEFICIARIA");
        }
        // TODO :cuando el endpoint esté protegido (rol SERVICIO/ADMIN) se puede permitir crear admins
        if (valor == Rol.ADMIN) {
            throw new DatosInvalidosException("No se pueden crear administradores por este endpoint");
        }
        return valor;
    }

    private String generarPasswordTemporal() {
        StringBuilder sb = new StringBuilder(LARGO_PASSWORD_TEMPORAL);
        for (int i = 0; i < LARGO_PASSWORD_TEMPORAL; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    public static class DatosInvalidosException extends RuntimeException {
        public DatosInvalidosException(String mensaje) {
            super(mensaje);
        }
    }

    public static class EmailEnUsoException extends RuntimeException {
        public EmailEnUsoException(String email) {
            super("Ya existe un usuario con el email " + email);
        }
    }
}
