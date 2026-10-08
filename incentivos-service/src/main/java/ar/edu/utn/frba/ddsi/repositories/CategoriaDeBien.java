package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaDeBien extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
