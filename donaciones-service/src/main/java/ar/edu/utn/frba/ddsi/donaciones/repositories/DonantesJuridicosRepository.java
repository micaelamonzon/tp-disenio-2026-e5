package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.common.Persona;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonantesJuridicosRepository  extends JpaRepository<PersonaJuridica, Long> {
}
