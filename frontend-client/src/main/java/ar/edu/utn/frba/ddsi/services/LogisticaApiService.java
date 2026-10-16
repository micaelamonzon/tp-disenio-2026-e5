package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;
import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.EntregaDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.RutaEntregaDTO;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import ar.edu.utn.frba.ddsi.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;


@Service
public class LogisticaApiService {

    private static final Logger log = LoggerFactory.getLogger(LogisticaApiService.class);
    private final WebApiCallerService apiCaller;
    private final String logisticaServiceUrl;

    public LogisticaApiService(@Value("${logistica.service.url}") String logisticaServiceUrl,
                               WebApiCallerService apiCaller) {
        this.logisticaServiceUrl = logisticaServiceUrl;
        this.apiCaller = apiCaller;
    }

    public List<CamionDTO> obtenerCamiones() {
        try {
            return apiCaller.getList(logisticaServiceUrl + "/logistica-service/camiones", CamionDTO.class);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a logistica-service", e);
            throw new RuntimeException("No se pudieron obtener los camiones", e);
        }
    }

    // Alta de un camión desde el panel de administración
    public void crearCamion(CamionDTO camion) {
        try {
            apiCaller.post(logisticaServiceUrl + "/logistica-service/camiones", camion, Void.class);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creando el camión {}", camion.getPatente(), e);
            throw new RuntimeException("No se pudo crear el camión", e);
        }
    }

    // Baja de un camión
    public void eliminarCamion(String id) {
        try {
            apiCaller.delete(logisticaServiceUrl + "/logistica-service/camiones/{id}", id);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error eliminando el camión {}", id, e);
            throw new RuntimeException("No se pudo eliminar el camión", e);
        }
    }

    public List<EntregaDTO> obtenerEntregas() {
        try {
            return apiCaller.getList(logisticaServiceUrl + "/logistica-service/entregas", EntregaDTO.class);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a logistica-service", e);
            throw new RuntimeException("No se pudieron obtener las entregas", e);
        }
    }

    public EntregaDTO obtenerEntregaPorId(String id) {
        try {
            return apiCaller.get(logisticaServiceUrl + "/logistica-service/entrega/{id}", EntregaDTO.class, id);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo entrega {}", id, e);
            throw new RuntimeException("No se pudo obtener la entrega", e);
        }
    }


    // Confirma la recepción de una entrega (req. 12). Logística guarda las URLs de las fotos,
    // pasa la entrega a ENTREGADA y avisa a donaciones para actualizar cada donación.
    public EntregaDTO confirmarRecepcion(String entregaId, List<String> fotosUrl, String responsableId) {
        try {
            return apiCaller.patch(
                    logisticaServiceUrl + "/logistica-service/entrega/{id}/confirmarRecepcion?responsableId={responsable}",
                    Map.of("fotosUrl", fotosUrl), EntregaDTO.class, entregaId, responsableId);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error confirmando la recepción de la entrega {}", entregaId, e);
            throw new RuntimeException("No se pudo confirmar la recepción", e);
        }
    }

    public List<RutaEntregaDTO> obtenerRutasDeDonante(Long idDonante,TipoDeDonante tipoDeDonante) {
        try {
            List<RutaEntregaDTO> rutas = apiCaller.getList(
                    logisticaServiceUrl + "/logistica-service/camiones/monitoreo/ruta/donante/{idDonante}/recorrido?tipoDonante={tipoDeDonante}",
                    RutaEntregaDTO.class, idDonante, tipoDeDonante);
            log.debug("Rutas del donante {}: {}", idDonante, rutas);
            return rutas != null ? rutas : List.of();
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo las rutas del donante {}", idDonante, e);
            return List.of();
        }
    }


    // Rutas de las entregas activas de una entidad, recortadas hasta su parada (req. 14)
    public List<RutaEntregaDTO> obtenerRutasDeEntidad(Long idEntidad) {
        try {
            List<RutaEntregaDTO> rutas = apiCaller.getList(
                    logisticaServiceUrl + "/logistica-service/camiones/monitoreo/ruta/entidad/{id}/recorrido",
                    RutaEntregaDTO.class, idEntidad);
            return rutas != null ? rutas : List.of();
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo las rutas de la entidad {}", idEntidad, e);
            throw new RuntimeException("No se pudieron obtener las rutas de la entidad", e);
        }
    }
}
