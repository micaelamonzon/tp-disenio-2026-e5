package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.SolicitudPlanificacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolicitudPlanificacionRepository extends MongoRepository<SolicitudPlanificacion, String> {

}