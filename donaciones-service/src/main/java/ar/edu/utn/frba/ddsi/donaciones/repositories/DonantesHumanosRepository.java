package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonantesHumanosRepository extends JpaRepository<PersonaHumana, Long> {


}
