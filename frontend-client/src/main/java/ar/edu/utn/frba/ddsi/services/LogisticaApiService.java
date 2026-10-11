package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.EntregaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.List;

@Service
public class LogisticaApiService {

    private static final Logger log = LoggerFactory.getLogger(LogisticaApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String logisticaServiceUrl;

    public LogisticaApiService(@Value("${logistica.service.url}") String logisticaServiceUrl) {
        this.logisticaServiceUrl = logisticaServiceUrl;
    }

    public List<CamionDTO> obtenerCamiones() {
        try {
            List<CamionDTO> camiones = webClient.get()
                    .uri(logisticaServiceUrl + "/logistica-service/camiones")
                    .retrieve()
                    .bodyToFlux(CamionDTO.class)
                    .collectList()
                    .block();
            return camiones != null ? camiones : List.of();
        } catch (Exception e) {
            log.error("Error llamando a logistica-service", e);
            throw new RuntimeException("No se pudieron obtener los camiones", e);
        }
    }

    public List<EntregaDTO> obtenerEntregas() {
        try {
            List<EntregaDTO> entregas = webClient.get()
                    .uri(logisticaServiceUrl + "/logistica-service/entregas")
                    .retrieve()
                    .bodyToFlux(EntregaDTO.class)
                    .collectList()
                    .block();
            return entregas != null ? entregas : List.of();
        } catch (Exception e) {
            log.error("Error llamando a logistica-service", e);
            throw new RuntimeException("No se pudieron obtener las entregas", e);
        }
    }

    public EntregaDTO obtenerEntregaPorId(String id) {
        try {
            return webClient.get()
                    .uri(logisticaServiceUrl + "/logistica-service/entrega/" + id)
                    .retrieve()
                    .bodyToMono(EntregaDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error obteniendo entrega {}", id, e);
            throw new RuntimeException("No se pudo obtener la entrega", e);
        }
    }


    // Confirma la recepción de una entrega (req. 12). Logística guarda las URLs de las fotos,
    // pasa la entrega a ENTREGADA y avisa a donaciones para actualizar cada donación.
    public EntregaDTO confirmarRecepcion(String entregaId, List<String> fotosUrl, String responsableId) {
        try {
            return webClient.patch()
                    .uri(logisticaServiceUrl + "/logistica-service/entrega/" + entregaId
                            + "/confirmarRecepcion?responsableId=" + responsableId)
                    .bodyValue(Map.of("fotosUrl", fotosUrl))
                    .retrieve()
                    .bodyToMono(EntregaDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error confirmando la recepción de la entrega {}", entregaId, e);
            throw new RuntimeException("No se pudo confirmar la recepción", e);
        }
    }
}