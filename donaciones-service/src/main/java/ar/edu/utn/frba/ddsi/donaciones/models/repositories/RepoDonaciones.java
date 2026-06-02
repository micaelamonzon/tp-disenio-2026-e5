package ar.edu.utn.frba.ddsi.donaciones.models.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepoDonaciones extends JpaRepository<Donacion, Long> {
    // Spring genera la query basándose en el parámetro Estado
    List<Donacion> findByEstado(Estado estado);

}
