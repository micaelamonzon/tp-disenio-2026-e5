package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.springframework.stereotype.Repository;
import java.util.List;


public interface DonacionesRepository {
    // Spring genera la query basándose en el parámetro Estado
   // List<Donacion> findByEstado(Estado estado);

    void deleteHumano(Long id);

    void deleteJuridico(Long id);
    List<DonacionSegmentada> findByEstado(String nombreEstado);
}
