package ar.edu.utn.frba.ddsi.donaciones.models.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class Persistente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean activo;

    @Column
    private LocalDateTime fechaAlta;

    @Column
    private LocalDate fechaModificacion;

    public Persistente() {
        this.fechaAlta = LocalDateTime.now();
        this.activo = true;
    }
}
