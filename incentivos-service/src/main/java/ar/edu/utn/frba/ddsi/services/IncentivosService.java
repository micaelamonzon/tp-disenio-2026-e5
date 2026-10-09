package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;

import java.util.List;

public interface IncentivosService {



    public List<InsigniaDTO> buscarInsigniasPorId(Long id,TipoDeDonante tipoDeDonante);
    public void agregarMisionADonante(Long id, TipoDeDonante tipoDeDonante, Long misionId);
    public MisionParticularDTO buscarMisionActualPorId(Long id, TipoDeDonante tipoDeDonante);

    public String publicarYDifundirInsignia(Long id, Insignia insignia, String tipoDeDonante);
    public List<MisionParticularDTO> obtenerMisionesCompletadas(Long id, TipoDeDonante tipoDeDonante);
    Boolean pasaSiguienteCategoria(Donante donante);
    public void pasaASiguienteMisionDeLaMismaCategoria(Donante donante);
    public void setearSiguienteCategoria(Donante donante);
    public PerfilDTO obtenerPerfilPorId(Long idEnDonaciones, TipoDeDonante tipoDeDonante);


    RankingMensual obtenerUltimoRanking();
    void calcularYGuardarRanking();

    MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante);

    public String procesarLogro(Long id, Insignia insignia, TipoDeDonante tipoDeDonante);

    MetricasSistemaDTO obtenerMetricasDelSistema();

    RankingMensualDTO obtenerUltimoRankingDTO();

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio);

    public CategoriaDeMisionDTO crearCategoriaMision(CategoriaDeMisionDTO dto);

    public MisionDTO crearMision(MisionDTO mision);
    public List<MisionFrontDTO> obtenerTodasLasMisionesPorId(Long id);

}
