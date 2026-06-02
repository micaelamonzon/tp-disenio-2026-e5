package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;

import java.util.List;

public interface IncentivosService {


    public List<MisionDTO> obtenerDonanteHumano(Long id);
    public List<MisionDTO> obtenerDonanteJuridico(Long id);
    public List<InsigniaDTO> buscarInsigniasPorId(Long id);

    public MisionDTO buscarMisionActualPorId(Long id);

    public String publicarYDifundirInsignia(Long idPersona, Insignia insignia);

    RankingMensual obtenerUltimoRanking();
    void calcularYGuardarRanking();

    MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante);

    public String procesarLogro(Long id, Insignia insignia, boolean esHumana);

    MetricasSistemaDTO obtenerMetricasDelSistema();

    RankingMensualDTO obtenerUltimoRankingDTO();

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio);
}
