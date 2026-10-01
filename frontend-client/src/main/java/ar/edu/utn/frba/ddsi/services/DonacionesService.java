package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonacionesService {

    private final DonacionesApiService donacionesApiService;

    public DonacionesService(DonacionesApiService donacionesApiService) {
        this.donacionesApiService = donacionesApiService;
    }

    public List<DonacionDTO> obtenerDonacionesDeHumano(Long idDonante) {
        return donacionesApiService.obtenerDonacionesDeHumano(idDonante);
    }
}
