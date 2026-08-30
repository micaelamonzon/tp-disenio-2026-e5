package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;

import java.util.List;

public interface IncentivosService {



    public List<InsigniaDTO> buscarInsigniasPorId(Long id,String tipoDeDonante);
    public void agregarMisionADonante(Long id, String tipoDeDonante,String tipoCategoria,MisionDTO misionDTO);
    public MisionDTO buscarMisionActualPorId(Long id, String tipoDeDonante);
    public void pedirDonantesAServiceDonaciones();
    public String publicarYDifundirInsignia(Long idPersona, Insignia insignia);
    public List<MisionDTO> obtenerMisionesCompletadas(Long id,String tipoDeDonante);

    RankingMensual obtenerUltimoRanking();
    void calcularYGuardarRanking();

    MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante);

    public String procesarLogro(Long id, Insignia insignia, String tipoDeDonante);

    MetricasSistemaDTO obtenerMetricasDelSistema();

    RankingMensualDTO obtenerUltimoRankingDTO();

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio);
}
