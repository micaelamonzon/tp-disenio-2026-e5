package ar.edu.utn.frba.ddsi.donaciones.repositories;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
