package ar.edu.utn.frba.ddsi.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador para iniciar sesión
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Hash BCrypt: la contraseña nunca se guarda en texto
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    // Id del donante (humano o jurídico) o de la entidad en donaciones-service.
    // Qué representa lo indica el rol. Es null para el administrador.
    @Column(name = "id_en_dominio")
    private Long idEnDominio;

    // Nombre para mostrar en el front
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta = LocalDateTime.now();

    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;

    @Column(name = "expiracion_token")
    private LocalDateTime expiracionToken;

    public Usuario(String email, String passwordHash, Rol rol, Long idEnDominio, String nombre) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.idEnDominio = idEnDominio;
        this.nombre = nombre;
    }
}
