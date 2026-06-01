package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.utils.GeneradorIdSecuencial;
import org.springframework.stereotype.Repository;

@Repository
public class PersonaHumanaRepository {
    private final GeneradorIdSecuencial generadorId;

    public PersonaHumanaRepository(GeneradorIdSecuencial generadorId) {
        this.generadorId = generadorId;
    }

    public void saveHumana (PersonaHumana persona) {
        if (persona.getId() == null) {
            persona.setId(generadorId.siguiente());
        }
    }
}
