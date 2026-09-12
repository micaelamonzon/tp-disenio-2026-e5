package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos.PropuestaMatch;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MatchRepository extends JpaRepository<PropuestaMatch, Long> {

}
