package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;

import java.util.List;
import java.util.Optional;

public interface IncentivosRepository {
    void guardarDonante(Donante donante);
    List<Donante> findAllDonantes();
    Donante buscarDonantePorId(Long id);
    void guardarRanking(RankingMensual ranking);
    List<RankingMensual> findAllRankings();

// tal vez esto sirva para las metricas

//    List<Object> findAll();
//    Object findById(Long id);
//    Object save(Object producto);
//    void delete(Object producto);
}



