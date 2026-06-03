package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;

import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MatchRepository  {
    PropuestaMatch findById(Long matcheoId);

    PropuestaMatch save(PropuestaMatch propuesta);
    // Hereda automáticamente .save(propuesta) para persistir en la base de datos
}
