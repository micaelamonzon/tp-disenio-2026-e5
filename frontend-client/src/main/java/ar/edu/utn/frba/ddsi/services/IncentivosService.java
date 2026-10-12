package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MisionFrontDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.PerfilDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncentivosService {

    private final IncentivosApiService incentivosApiService;

    public IncentivosService(IncentivosApiService incentivosApiService) {
        this.incentivosApiService = incentivosApiService;
    }

    public List<MisionFrontDTO> obtenerMisiones(Long idDonante) {
        return incentivosApiService.obtenerTodasLasMisiones(idDonante);
    }
    public PerfilDTO obtenerPerfil(Long idDonante) {
        return incentivosApiService.obtenerPerfil(idDonante);
    }
    public void comenzarMision(Long idDonante, Long misionId) {
        incentivosApiService.comenzarMision(idDonante, misionId);
    }
    public RankingMensualDTO obtenerUltimoRanking() {
        return incentivosApiService.obtenerUltimoRanking();
    }

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio) {
        return incentivosApiService.buscarRankings(mes, anio);
    }
}
