package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoMisionRepository extends JpaRepository<Estado, Long> {
}
