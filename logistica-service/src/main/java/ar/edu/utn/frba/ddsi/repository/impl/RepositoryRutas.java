package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.Ruta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoryRutas extends MongoRepository<Ruta, String> {
}