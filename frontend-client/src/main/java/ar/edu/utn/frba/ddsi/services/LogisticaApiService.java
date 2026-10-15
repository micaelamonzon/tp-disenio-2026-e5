package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;
import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.EntregaDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.RutaEntregaDTO;
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

    // Alta de un camión desde el panel de administración.
    // SUPUESTO: logistica-service expone POST /logistica-service/camiones. Si la ruta es otra, cambiarla acá.
    public void crearCamion(CamionDTO camion) {
        try {
            webClient.post()
                    .uri(logisticaServiceUrl + "/logistica-service/camiones")
                    .bodyValue(camion)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error creando el camión {}", camion.getPatente(), e);
            throw new RuntimeException("No se pudo crear el camión", e);
        }
    }

    // Baja de un camión.
    // SUPUESTO: logistica-service expone DELETE /logistica-service/camiones/{id}.
    public void eliminarCamion(String id) {
        try {
            webClient.delete()
                    .uri(logisticaServiceUrl + "/logistica-service/camiones/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error eliminando el camión {}", id, e);
            throw new RuntimeException("No se pudo eliminar el camión", e);
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

    public List<RutaEntregaDTO> obtenerRutasDeDonante(Long idDonante, TipoDeDonante tipoDeDonante) {
        try {
            List<RutaEntregaDTO> rutas = webClient.get()
                    .uri(logisticaServiceUrl + "/logistica-service/camiones/monitoreo/ruta/donante/"
                            + idDonante + "/recorrido" + "?tipoDonante="+tipoDeDonante)
                    .retrieve()
                    .bodyToFlux(RutaEntregaDTO.class)
                    .collectList()
                    .block();

            if (rutas == null) {
                return List.of();
            }
            log.debug("Rutas del donante {}: {}", idDonante, rutas);
            return rutas;
        } catch (Exception e) {
            log.error("Error obteniendo las rutas del donante {}", idDonante, e);
            return List.of();
        }
    }


    // Rutas de las entregas activas de una entidad, recortadas hasta su parada (req. 14)
    public List<RutaEntregaDTO> obtenerRutasDeEntidad(Long idEntidad) {
        try {
            List<RutaEntregaDTO> rutas = webClient.get()
                    .uri(logisticaServiceUrl + "/logistica-service/camiones/monitoreo/ruta/entidad/"
                            + idEntidad + "/recorrido")
                    .retrieve()
                    .bodyToFlux(RutaEntregaDTO.class)
                    .collectList()
                    .block();
            return rutas != null ? rutas : List.of();
        } catch (Exception e) {
            log.error("Error obteniendo las rutas de la entidad {}", idEntidad, e);
            throw new RuntimeException("No se pudieron obtener las rutas de la entidad", e);
        }
    }
}