package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Donacion {
    private String descripcion;
    private Estado estado;
    private PersonaDonanteDTO donante;
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