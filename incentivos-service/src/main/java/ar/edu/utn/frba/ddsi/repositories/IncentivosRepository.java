package ar.edu.utn.frba.ddsi.repositories;

import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IncentivosRepository extends JpaRepository<Donante, Long> {

    @Query("SELECT d FROM Donante d WHERE d.idEnDonaciones= :id and d.tipoDeDonante= :tipoDeDonante")
    public Donante findByIdEnDonaciones(Long id, TipoDeDonante tipoDeDonante);
}



