package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MisionFrontDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.PerfilDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;
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


    public List<MisionFrontDTO> obtenerTodasLasMisiones(Long idDonante, TipoDeDonante tipo) {
        try {
            List<MisionFrontDTO> misiones = webClient.get()
                    .uri(incentivosServiceUrl + "/misiones/todas/{id}?tipoDeDonante={tipo}",
                            idDonante, tipo)
                    .retrieve()
                    .bodyToFlux(MisionFrontDTO.class)
                    .collectList()
                    .block();
            System.out.println("Obtener todas las misiones  con" + idDonante + tipo);
            return misiones != null ? misiones : List.of();
        } catch (Exception e) {
            log.error("Error llamando a incentivos-service", e);
            throw new RuntimeException("No se pudieron obtener las misiones", e);
        }
    }

    public PerfilDTO obtenerPerfil(Long idDonante, TipoDeDonante tipo) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/perfil/{id}")
                    .queryParam("tipoDeDonante", tipo)
                    .buildAndExpand(idDonante)
                    .toUri();
            System.out.println("Obtener perfil con " + uri);
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

    public void comenzarMision(Long idDonante, Long misionId, TipoDeDonante tipo) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/donante/{id}/mision/{misionId}")
                    .queryParam("tipoDeDonante", tipo)
                    .buildAndExpand(idDonante, misionId)
                    .toUri();
        System.out.println("Comenzar mision con" + uri);
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

    // ==========================================
    // MÉTODOS DE RANKING Y MÉTRICAS
    // ==========================================

    public RankingMensualDTO obtenerUltimoRanking() {
        try {
            URI uri = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/ranking/ultimo")
                    .build()
                    .toUri();

            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(RankingMensualDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error obteniendo el último ranking", e);
            return null; // O manejar según necesidad
        }
    }

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/ranking/historial");

            if (mes != null) builder.queryParam("mes", mes);
            if (anio != null) builder.queryParam("anio", anio);

            List<RankingMensualDTO> list = webClient.get()
                    .uri(builder.build().toUri())
                    .retrieve()
                    .bodyToFlux(RankingMensualDTO.class)
                    .collectList()
                    .block();
            return list != null ? list : List.of();
        } catch (Exception e) {
            log.error("Error buscando historial de rankings", e);
            return List.of();
        }
    }
}
