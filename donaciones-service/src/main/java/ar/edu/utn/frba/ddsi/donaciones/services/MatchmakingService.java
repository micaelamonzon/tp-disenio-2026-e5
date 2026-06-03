package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.*;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonacionesRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.MatchRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.NecesidadesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final MatchRepository matchRepository;
    private final NecesidadesRepository necesidadesRepository;
    private final DonacionesRepository donacionesRepository;
    private final MotorDeMatchmaking motorMatchmaking;
    private final List<Strategy_AlgoritmosMatchmaking> estrategiasActivas = new ArrayList<>();


    public PropuestaMatch obtenerPropuestaPorId(Long matcheoId ) {
        return matchRepository.findById(matcheoId);
    }

    public void ejecutarProcesoMatchmaking() {
        List<Donacion> donacionesPendientes = donacionesRepository.findByEstado(Estado.EN_DEPOSITO);
        List<Necesidad> necesidadesActivas = necesidadesRepository.findByEstaSatisfechaFalse();

        if (donacionesPendientes.isEmpty() || necesidadesActivas.isEmpty() || estrategiasActivas.isEmpty()) {
            return;
        }

        for (Donacion donacion : donacionesPendientes) {
            PropuestaMatch propuesta = new PropuestaMatch();
            propuesta.setDonacion(donacion);

            List<List<Necesidad>> todosLosRankings = new ArrayList<>();

            for (Strategy_AlgoritmosMatchmaking estrategia : estrategiasActivas) {
                motorMatchmaking.setEstrategiaActual(estrategia);
                List<Necesidad> ranking = motorMatchmaking.generarRanking(donacion, necesidadesActivas);

                todosLosRankings.add(ranking);

                RankingPorAlgoritmo rankingIndividual = new RankingPorAlgoritmo();
                rankingIndividual.setNombreAlgoritmo(estrategia.getClass().getSimpleName());
                rankingIndividual.setNecesidades(ranking);
                propuesta.getRankingsIndividuales().add(rankingIndividual);
            }

            List<Necesidad> coincidencias = new ArrayList<>(todosLosRankings.get(0));
            for (int i = 1; i < todosLosRankings.size(); i++) {
                coincidencias.retainAll(todosLosRankings.get(i));
            }

            if (!coincidencias.isEmpty()) {
                propuesta.setRankingConjunto(coincidencias);
                propuesta.getRankingsIndividuales().clear();
            }

            matchRepository.save(propuesta);
        }
    }


    public PropuestaMatch seleccionarNecesidad(Long matcheoId, Long necesidadId) {
        if (necesidadId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar una necesidadId");
        }

        PropuestaMatch propuesta = matchRepository.findById(matcheoId);


        Necesidad necesidad = necesidadesRepository.findById(necesidadId);

        if (!perteneceAAlgunRanking(propuesta, necesidadId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La necesidad seleccionada no pertenece a los rankings generados");
        }

        propuesta.setNecesidadSeleccionada(necesidad);

        //TODO Poner comportamiento de asignación con PosibleDonacion

        return matchRepository.save(propuesta);
    }

    private boolean perteneceAAlgunRanking(PropuestaMatch propuesta, Long necesidadId) {
        return contieneNecesidad(propuesta.getRankingConjunto(), necesidadId)
                || propuesta.getRankingsIndividuales().stream()
                .map(RankingPorAlgoritmo::getNecesidades)
                .anyMatch(ranking -> contieneNecesidad(ranking, necesidadId));
    }

    private boolean contieneNecesidad(List<Necesidad> necesidades, Long necesidadId) {
        return necesidades.stream()
                .map(Necesidad::getId)
                .anyMatch(necesidadId::equals);
    }
}
