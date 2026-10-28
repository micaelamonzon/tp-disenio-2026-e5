package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonacionesSinSegmentarRepository extends JpaRepository<DonacionSinSegmentar, Long> {
}
