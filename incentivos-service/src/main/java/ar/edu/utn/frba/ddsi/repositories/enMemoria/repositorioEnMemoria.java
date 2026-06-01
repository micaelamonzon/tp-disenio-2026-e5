package ar.edu.utn.frba.ddsi.repositories.enMemoria;

import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class repositorioEnMemoria implements IncentivosRepository {
    //para Emi
    private final List<Object> listaDeMetricas = new ArrayList<>();
    private final List<Donante> donantes = new ArrayList<>();
}
