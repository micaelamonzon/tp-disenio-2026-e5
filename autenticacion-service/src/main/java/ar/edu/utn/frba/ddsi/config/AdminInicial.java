package ar.edu.utn.frba.ddsi.config;

import ar.edu.utn.frba.ddsi.models.Rol;
import ar.edu.utn.frba.ddsi.models.Usuario;
import ar.edu.utn.frba.ddsi.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Crea el administrador inicial al arrancar, solo si todavía no existe.
@Component
public class AdminInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminInicial(UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        @Value("${admin.email}") String email,
                        @Value("${admin.password}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            log.info("Admin inicial {} ya existe", email);
            return;
        }
        usuarioRepository.save(new Usuario(
                email, passwordEncoder.encode(password), Rol.ADMIN, null, "Administrador"));
        log.info("Admin inicial {} creado", email);
    }
}
