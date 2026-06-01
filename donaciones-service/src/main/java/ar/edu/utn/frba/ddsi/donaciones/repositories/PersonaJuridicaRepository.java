package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.utils.GeneradorIdSecuencial;

public class PersonaJuridicaRepository {
    private final GeneradorIdSecuencial generadorId;

    public PersonaJuridicaRepository(GeneradorIdSecuencial generadorId) {
        this.generadorId = generadorId;
    }

    public void saveJuridica(PersonaJuridica juridica) {
        if (juridica.getId() == null) {
            juridica.setId(generadorId.siguiente());
        }
    }
}
