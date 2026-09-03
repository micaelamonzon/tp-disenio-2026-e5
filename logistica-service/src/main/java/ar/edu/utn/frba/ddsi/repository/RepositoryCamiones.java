package ar.edu.utn.frba.ddsi.repository;

import ar.edu.utn.frba.ddsi.models.entities.Camion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryCamiones extends JpaRepository<Camion, Long> {
}
