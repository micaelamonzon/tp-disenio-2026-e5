package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.notificaciones.NotificacionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

// Cliente del front hacia notificaciones-service.
// Por ahora sin token, igual que el resto de los ApiService: cuando esté
// el servicio de autenticación, se pasa a usar WebApiCallerService.
@Service
public class NotificacionesApiService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String notificacionesServiceUrl;

    public NotificacionesApiService(@Value("${notificaciones.service.url}") String notificacionesServiceUrl) {
        this.notificacionesServiceUrl = notificacionesServiceUrl;
    }

    public List<NotificacionDTO> obtenerNotificacionesDeEntidad(Long entidadId) {
        return obtenerLista("/entidades/" + entidadId + "/notificaciones");
    }

    public List<NotificacionDTO> obtenerNotificacionesDeDonante(Long donanteId) {
        return obtenerLista("/donantes/" + donanteId + "/notificaciones");
    }

    // Los jurídicos tienen su propio endpoint: sus ids son de otra tabla y podrían
    // coincidir con los de las personas humanas
    public List<NotificacionDTO> obtenerNotificacionesDeDonanteJuridico(Long donanteJuridicoId) {
        return obtenerLista("/juridicos/" + donanteJuridicoId + "/notificaciones");
    }

    // Las dos llamadas son iguales salvo la ruta, así que comparten este método
    private List<NotificacionDTO> obtenerLista(String ruta) {
        try {
            List<NotificacionDTO> notificaciones = webClient.get()
                    .uri(notificacionesServiceUrl + ruta)
                    .retrieve()
                    .bodyToFlux(NotificacionDTO.class)
                    .collectList()
                    .block();
            return notificaciones != null ? notificaciones : List.of();
        } catch (Exception e) {
            log.error("Error llamando a notificaciones-service en {}", ruta, e);
            throw new RuntimeException("No se pudieron obtener las notificaciones", e);
        }
    }
}