package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.RankingPorAlgoritmo;
import ar.edu.utn.frba.ddsi.donaciones.models.repositories.RepoMatcheo;
import ar.edu.utn.frba.ddsi.donaciones.models.repositories.RepoNecesidades;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchmakingService {
    private final RepoMatcheo repoMatcheo;
    private final RepoNecesidades repoNecesidades;

    public PropuestaMatch seleccionarNecesidad(Long matcheoId, Long necesidadId) {
        if (necesidadId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar una necesidadId");
        }

        PropuestaMatch propuesta = repoMatcheo.findById(matcheoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propuesta de matcheo no encontrada"));

        Necesidad necesidad = repoNecesidades.findById(necesidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Necesidad no encontrada"));

        if (!perteneceAAlgunRanking(propuesta, necesidadId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La necesidad seleccionada no pertenece a los rankings generados");
        }

        propuesta.setNecesidadSeleccionada(necesidad);
        return repoMatcheo.save(propuesta);
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
