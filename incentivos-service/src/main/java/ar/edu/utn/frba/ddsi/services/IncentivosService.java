package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;

import java.util.List;

public interface IncentivosService {



    public List<InsigniaDTO> buscarInsigniasPorId(Long id);
    public void agregarMisionADonante(Long id, MisionDTO misionesDTO);
    public MisionDTO buscarMisionActualPorId(Long id);
    public void pedirDonantesAServiceDonaciones();
    public String publicarYDifundirInsignia(Long idPersona, Insignia insignia);
    public List<MisionDTO> obtenerMisionesCompletadas(Long id);

    RankingMensual obtenerUltimoRanking();
    void calcularYGuardarRanking();

    MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante);

    public String procesarLogro(Long id, Insignia insignia, boolean esHumana);

    MetricasSistemaDTO obtenerMetricasDelSistema();

    RankingMensualDTO obtenerUltimoRankingDTO();

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio);
}
