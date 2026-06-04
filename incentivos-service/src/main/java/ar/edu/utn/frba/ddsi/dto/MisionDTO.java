package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDate;
import java.util.List;

public record MisionDTO(
       String nombre,
       @JsonAlias("estadoDeMision") EstadoDeMision estado,
       LocalDate fechaCompletada
) {
}
