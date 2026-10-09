package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MisionFrontDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.PerfilDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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

    public PerfilDTO obtenerPerfil(Long idDonante) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/perfil/{id}")
                    .queryParam("tipoDeDonante", "HUMANO")
                    .buildAndExpand(idDonante)
                    .toUri();

            PerfilDTO perfil = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(PerfilDTO.class)
                    .block();

            if (perfil == null) {
                throw new IllegalStateException("El servicio de incentivos devolvió un perfil vacío");
            }
            return perfil;
        } catch (Exception e) {
            log.error("Error llamando a incentivos-service", e);
            throw new RuntimeException("No se pudo obtener el perfil", e);
        }
    }

    public void comenzarMision(Long idDonante, Long misionId) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/donante/{id}/mision/{misionId}")
                    .queryParam("tipoDeDonante", "HUMANO")
                    .buildAndExpand(idDonante, misionId)
                    .toUri();

            webClient.post()
                    .uri(uri)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error comenzando misión {} del donante {}", misionId, idDonante, e);
            throw new RuntimeException("No se pudo comenzar la misión", e);
        }
    }
}
