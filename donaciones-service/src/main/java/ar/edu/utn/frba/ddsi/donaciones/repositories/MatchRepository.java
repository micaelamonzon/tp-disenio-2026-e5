package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<PropuestaMatch, Long> {
    // Hereda automáticamente .save(propuesta) para persistir en la base de datos
}
