package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Donacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Transient
    private PersonaDonanteDTO donante;

    @Transient
    private Bien bien;

    private LocalDate fechaEntrega;

    public Donacion(String descripcion, PersonaDonanteDTO donante, Bien bien, LocalDate fechaEntrega) {
        this.descripcion = descripcion;
        this.donante = donante;
        this.bien = bien;
        this.estado = Estado.EN_DEPOSITO;
        this.fechaEntrega = fechaEntrega;
    }
}
