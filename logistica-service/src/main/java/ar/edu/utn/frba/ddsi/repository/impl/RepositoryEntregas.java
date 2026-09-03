package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositoryEntregas extends JpaRepository<Entrega, Long> {
    List<Entrega> findByEstadoActual(EstadoEntrega estado);
}