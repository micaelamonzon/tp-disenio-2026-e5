package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MisionParticularRepository extends JpaRepository<MisionParticular, Long> {


    public List<MisionParticular> findByDonanteId(Long donanteId);
}
