package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface RepositoryEntregas extends MongoRepository<Entrega, String> {
    List<Entrega> findByEstadoActual(EstadoEntrega estado);
    List<Entrega> findByDonacionesIdsIn(Collection<Long> donacionesIds);
    List<Entrega> findByCamionId(String camionId);
}