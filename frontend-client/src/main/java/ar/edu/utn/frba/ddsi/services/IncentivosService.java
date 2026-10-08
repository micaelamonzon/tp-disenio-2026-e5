package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.misiones.MisionFrontDTO;
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
}
