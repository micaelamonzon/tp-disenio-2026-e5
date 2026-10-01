package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class DonacionesApiService {

    private static final Logger log = LoggerFactory.getLogger(DonacionesApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String donacionesServiceUrl;

    public DonacionesApiService(@Value("${donaciones.service.url}") String donacionesServiceUrl) {
        this.donacionesServiceUrl = donacionesServiceUrl;
    }

    /** GET /servicioDeDonaciones/donaciones/humano/{id} */
    public List<DonacionDTO> obtenerDonacionesDeHumano(Long idDonante) {
        try {
            List<DonacionDTO> donaciones = webClient.get()
                    .uri(donacionesServiceUrl + "/donaciones/humano/{id}", idDonante)
                    .retrieve()
                    .bodyToFlux(DonacionDTO.class)
                    .collectList()
                    .block();
            return donaciones != null ? donaciones : List.of();
        } catch (WebClientResponseException.NotFound e) {
            throw new DonanteNoEncontradoException(idDonante);
        } catch (Exception e) {
            log.error("Error llamando a donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las donaciones", e);
        }
    }

    public static class DonanteNoEncontradoException extends RuntimeException {
        public DonanteNoEncontradoException(Long id) {
            super("No encontramos al donante " + id + ".");
        }
    }
}