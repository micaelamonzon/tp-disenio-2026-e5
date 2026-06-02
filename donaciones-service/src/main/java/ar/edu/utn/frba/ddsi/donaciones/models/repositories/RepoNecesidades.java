package ar.edu.utn.frba.ddsi.donaciones.models.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepoNecesidades extends JpaRepository<Necesidad, Long> {
    // Spring interpreta "False" al final y busca registros donde estaSatisfecha sea false
    List<Necesidad> findByEstaSatisfechaFalse();
}
