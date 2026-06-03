package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


public interface NecesidadesRepository  {
    // Spring interpreta "False" al final y busca registros donde estaSatisfecha sea false
    List<Necesidad> findByEstaSatisfechaFalse();

    Necesidad findById(Long necesidadId);
}
