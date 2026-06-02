package ar.edu.utn.frba.ddsi.donaciones.models.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoMatcheo extends JpaRepository<PropuestaMatch, Long> {
    // Hereda automáticamente .save(propuesta) para persistir en la base de datos
}
