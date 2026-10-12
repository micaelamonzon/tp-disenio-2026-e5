package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.Donacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositoryDonacion extends MongoRepository<Donacion, String> {

    Donacion findByIdEnDonaciones(Long idEnDonaciones);

    List<Donacion> findByDonanteId(Long donanteId);

    
}
