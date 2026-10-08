package ar.edu.utn.frba.ddsi.notificaciones.models.repositories;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Donante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonanteRepository extends JpaRepository<Donante, Long> {
}