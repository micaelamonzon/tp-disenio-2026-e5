package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MetricasImpactoDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MisionFrontDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.PerfilDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncentivosService {

    private final IncentivosApiService incentivosApiService;

    public IncentivosService(IncentivosApiService incentivosApiService) {
        this.incentivosApiService = incentivosApiService;
    }

    public List<MisionFrontDTO> obtenerMisiones(Long idDonante, TipoDeDonante tipo) {
        return incentivosApiService.obtenerTodasLasMisiones(idDonante,  tipo);
    }
    public PerfilDTO obtenerPerfil(Long idDonante, TipoDeDonante tipo) {
        return incentivosApiService.obtenerPerfil(idDonante, tipo);
    }
    public MetricasImpactoDTO obtenerMetricas(Long idDonante, TipoDeDonante tipo) {
        return incentivosApiService.obtenerMetricas(idDonante, tipo);
    }

    public void comenzarMision(Long idDonante, Long misionId,TipoDeDonante tipo) {
        incentivosApiService.comenzarMision(idDonante, misionId,  tipo);
    }
    public RankingMensualDTO obtenerUltimoRanking() {
        return incentivosApiService.obtenerUltimoRanking();
    }

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio) {
        return incentivosApiService.buscarRankings(mes, anio);
    }
}
