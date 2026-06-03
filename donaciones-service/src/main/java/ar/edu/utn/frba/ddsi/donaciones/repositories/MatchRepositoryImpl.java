package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import org.springframework.stereotype.Repository;

@Repository
public class MatchRepositoryImpl implements MatchRepository {
    @Override
    public PropuestaMatch findById(Long matcheoId){
        return null;
    }

    @Override
    public PropuestaMatch save(PropuestaMatch propuesta){
        return null;
    }
}
