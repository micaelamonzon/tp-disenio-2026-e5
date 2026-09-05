package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingMensualRepository
    extends JpaRepository<RankingMensual, Long>{
        Optional<RankingMensual> findTopByOrderByFechaDesc();

        List<RankingMensual> findAll();
    }

