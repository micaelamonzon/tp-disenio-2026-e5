package ar.edu.utn.frba.ddsi.repository;

import ar.edu.utn.frba.ddsi.models.entities.Camion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoryCamiones extends MongoRepository<Camion, String> {
}