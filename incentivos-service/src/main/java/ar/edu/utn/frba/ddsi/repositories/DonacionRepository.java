package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonacionRepository extends JpaRepository<DonacionSegmentada, Long> {

    DonacionSegmentada findByIdEnDonaciones(Long idEnDonaciones);
}
