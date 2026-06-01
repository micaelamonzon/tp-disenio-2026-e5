package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;

import java.util.List;
import java.util.Optional;

public interface DonacionesRepository {

    List<PersonaHumana> findAllHumanos();
    List<PersonaJuridica> findAllJuridicos();

    PersonaHumana humanoFindById(Long id);
    PersonaJuridica juridicaFindById(Long id);

    PersonaJuridica saveJuridica(PersonaJuridica donante);
    PersonaHumana saveHumana(PersonaHumana donante);

    void deleteJuridica(PersonaJuridica donante);
    void deleteHumana(PersonaHumana donante);
}
