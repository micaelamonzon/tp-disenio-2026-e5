package ar.edu.utn.frba.ddsi.notificaciones.models.repositories;

import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Notificaciones de un donante, de la más nueva a la más vieja
    // "Donante_Id" = entra a la relación donante y filtra por su id
    List<Notificacion> findByDonante_IdOrderByFechaEnvioDesc(Long donanteId);

    // Notificaciones de una entidad beneficiaria, de la más nueva a la más vieja
    List<Notificacion> findByEntidadBeneficiariaIdOrderByFechaEnvioDesc(Long entidadBeneficiariaId);
}