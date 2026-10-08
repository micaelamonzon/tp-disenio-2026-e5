package ar.edu.utn.frba.ddsi.models.entities.persistente;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class Persistente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean activo;

    @Column
    private LocalDateTime fechaAlta;

    @Column
    private LocalDateTime fechaModificacion;

    @Column
    private String nombreDeUsuario;

    public Persistente() {
        this.fechaAlta = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.activo = true;
    }
}
