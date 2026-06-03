package ar.edu.utn.frba.ddsi.repositories.enMemoria;

import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class repositorioEnMemoria implements IncentivosRepository {
    //para Emi
    private final List<Object> listaDeMetricas = new ArrayList<>();
    private final List<Donante> donantes = new ArrayList<>();

    private final List<RankingMensual> historialRankings = new ArrayList<>();
    @Override
    public void guardarDonante(Donante donante){
        donantes.removeIf(d -> d.getId()
                != null && d.getId()
                .equals(donante.getId()));
        Long id = Long.valueOf(this.donantes.size());
        donante.setId(id);
        donantes.add(donante);
    }

    @Override
    public List<Donante> findAllDonantes() {
        return donantes;
    }

    @Override
    public Donante buscarDonantePorId(Long id){
        return donantes.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void guardarRanking(RankingMensual ranking) {
        this.historialRankings.add(ranking);
    }
    @Override
    public List<RankingMensual> findAllRankings() {
        return new ArrayList<>(this.historialRankings);
    }
}
