package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.SolicitudPlanificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudPlanificacionRepository extends MongoRepository<SolicitudPlanificacion, String> {

}