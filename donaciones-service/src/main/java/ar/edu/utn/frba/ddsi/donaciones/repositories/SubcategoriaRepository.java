package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    Optional<Subcategoria> findByNombreIgnoreCase(String nombre);
}