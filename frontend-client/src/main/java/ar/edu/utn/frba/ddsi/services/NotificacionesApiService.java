package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.notificaciones.NotificacionDTO;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import ar.edu.utn.frba.ddsi.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

// Cliente del front hacia notificaciones-service.
// Las llamadas pasan por WebApiCallerService: manda el token del usuario logueado
// (notificaciones lo exige y solo deja ver las propias) y lo renueva si venció
@Service
public class NotificacionesApiService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesApiService.class);
    private final WebApiCallerService apiCaller;
    private final String notificacionesServiceUrl;

    public NotificacionesApiService(@Value("${notificaciones.service.url}") String notificacionesServiceUrl,
                                    WebApiCallerService apiCaller) {
        this.notificacionesServiceUrl = notificacionesServiceUrl;
        this.apiCaller = apiCaller;
    }

    public List<NotificacionDTO> obtenerNotificacionesDeEntidad(Long entidadId) {
        return obtenerLista("/entidades/{id}/notificaciones", entidadId);
    }

    public List<NotificacionDTO> obtenerNotificacionesDeDonante(Long donanteId) {
        return obtenerLista("/donantes/{id}/notificaciones", donanteId);
    }

    // Los jurídicos tienen su propio endpoint: sus ids son de otra tabla y podrían
    // coincidir con los de las personas humanas
    public List<NotificacionDTO> obtenerNotificacionesDeDonanteJuridico(Long donanteJuridicoId) {
        return obtenerLista("/juridicos/{id}/notificaciones", donanteJuridicoId);
    }

    // Las llamadas son iguales salvo la ruta, así que comparten este método
    private List<NotificacionDTO> obtenerLista(String ruta, Long id) {
        try {
            List<NotificacionDTO> notificaciones = apiCaller.getList(
                    notificacionesServiceUrl + ruta, NotificacionDTO.class, id);
            return notificaciones != null ? notificaciones : List.of();
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a notificaciones-service en {}", ruta, e);
            throw new RuntimeException("No se pudieron obtener las notificaciones", e);
        }
    }
}
