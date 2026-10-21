package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.NecesidadRankingDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PropuestaMatchDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.RankingAlgoritmoDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonacionesRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.MatchRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.NecesidadesRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private final EstadoDonacionServiceimpl estadoDonacionService;
    private final List<Strategy_AlgoritmosMatchmaking> estrategiasActivas;

    private final NotificacionesPublisher notificacionesPublisher;

    public PropuestaMatch obtenerPropuestaPorId(Long matcheoId ) {
        return matchRepository.findById(matcheoId).orElseThrow();
    }

    @Transactional
    public void ejecutarProcesoMatchmaking() {
        List<DonacionSegmentada> donacionesPendientes = donacionesRepository.findByEstado(EstadoDonacion.EN_DEPOSITO);
        List<Necesidad> necesidadesActivas = necesidadesRepository.findByEstaSatisfechaFalse();

        if (donacionesPendientes.isEmpty() || necesidadesActivas.isEmpty() || estrategiasActivas.isEmpty()) {
            return;
        }

        for (DonacionSegmentada donacion : donacionesPendientes) {
            // Descarta las propuestas de ejecuciones anteriores de esta donación
            matchRepository.deleteAll(matchRepository.findByDonacionId(donacion.getId()));

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

    public PropuestaMatch obtenerPropuestaDeDonacion(Long donacionId) {
        return matchRepository.findFirstByDonacionIdOrderByIdDesc(donacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No hay propuesta de matchmaking para la donación " + donacionId));
    }

    @Transactional(readOnly = true)
    public PropuestaMatchDTO obtenerPropuestaDTODeDonacion(Long donacionId) {
        return toPropuestaDTO(obtenerPropuestaDeDonacion(donacionId));
    }

    private PropuestaMatchDTO toPropuestaDTO(PropuestaMatch p) {
        return new PropuestaMatchDTO(
                p.getId(),
                p.getDonacion().getId(),
                p.getNecesidadSeleccionada() != null ? p.getNecesidadSeleccionada().getId() : null,
                p.getRankingConjunto().stream().map(this::toNecesidadDTO).toList(),
                p.getRankingsIndividuales().stream()
                        .map(r -> new RankingAlgoritmoDTO(r.getNombreAlgoritmo(),
                                r.getNecesidades().stream().map(this::toNecesidadDTO).toList()))
                        .toList());
    }

    private NecesidadRankingDTO toNecesidadDTO(Necesidad n) {
        var entidad = n.getEntidadBeneficiaria();
        return new NecesidadRankingDTO(n.getId(), n.tipo(), n.getDescripcion(),
                entidad != null ? entidad.getId() : null,
                entidad != null ? entidad.getRazonSocial() : null,
                n.cantidadSolicitada(), n.cantidadRecibida());
    }


    public PropuestaMatch seleccionarNecesidad(Long matcheoId, Long necesidadId, String responsableId) {
        if (necesidadId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar una necesidadId");
        }

        if (responsableId == null || responsableId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar un responsableId");
        }

        PropuestaMatch propuesta = matchRepository.findById(matcheoId).orElseThrow();


        Necesidad necesidad = necesidadesRepository.findById(necesidadId).orElseThrow();

        if (!perteneceAAlgunRanking(propuesta, necesidadId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La necesidad seleccionada no pertenece a los rankings generados");
        }

        propuesta.setNecesidadSeleccionada(necesidad);

        estadoDonacionService.asignar(propuesta.getDonacion().getId(), necesidad, responsableId);

        // Evento 2: Notificar a la entidad beneficiaria que se le asignó una donación
        if (necesidad.getEntidadBeneficiaria() != null &&
                !necesidad.getEntidadBeneficiaria().getRepresentantes().isEmpty()) {
            MedioDeNotificacion medio = necesidad.getEntidadBeneficiaria()
                    .getRepresentantes().get(0)
                    .getMediosDeNotificacion().get(0);
            notificacionesPublisher.publicarParaEntidad(
                    necesidad.getEntidadBeneficiaria().getId(),
                    medio.getContacto(),
                    "Se le asignó una nueva donación a su entidad beneficiaria",
                    medio.getTipoDeNotificacion().name()
            );
        }

        // Evento 3: Notificar al donante (humano o jurídico) que su donación fue asignada
        DonacionSegmentada donacion = propuesta.getDonacion();
        if (donacion != null) {
            String aviso = "Tu donación de " + donacion.getSubcategoria().getNombre()
                    + " fue asignada a una entidad beneficiaria";
            if (donacion.getDonanteHumano() != null) {
                notificacionesPublisher.publicarParaDonante(donacion.getDonanteHumano().getId(), aviso);
            } else if (donacion.getDonanteJuridico() != null) {
                notificacionesPublisher.publicarParaDonanteJuridico(donacion.getDonanteJuridico().getId(), aviso);
            }
        }

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
