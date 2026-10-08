package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.misiones.MisionFrontDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class IncentivosApiService {
    private static final Logger log = LoggerFactory.getLogger(IncentivosApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String incentivosServiceUrl;

    public IncentivosApiService(@Value("${incentivos.service.url}") String incentivosServiceUrl) {
        this.incentivosServiceUrl = incentivosServiceUrl;
    }


    public List<MisionFrontDTO> obtenerTodasLasMisiones(Long idDonante) {
        try {
            List<MisionFrontDTO> misiones = webClient.get()
                    .uri(incentivosServiceUrl + "/misiones/todas/{id}", idDonante)
                    .retrieve()
                    .bodyToFlux(MisionFrontDTO.class)
                    .collectList()
                    .block();
            return misiones != null ? misiones : List.of();
        } catch (Exception e) {
            log.error("Error llamando a incentivos-service", e);
            throw new RuntimeException("No se pudieron obtener las misiones", e);
        }
    }

}
