package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos.PropuestaMatch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MatchRepository extends JpaRepository<PropuestaMatch, Long> {
    List<PropuestaMatch> findByDonacionId(Long donacionId);

    // La más reciente de una donación
    Optional<PropuestaMatch> findFirstByDonacionIdOrderByIdDesc(Long donacionId);
}
