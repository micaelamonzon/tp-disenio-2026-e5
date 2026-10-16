package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ar.edu.utn.frba.ddsi.dto.EntidadContactoDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaContactoDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// Cliente de logística hacia donaciones-service.
@Component
public class DonacionesClient {

    private static final String HEADER_API_TOKEN = "X-Api-Token";

    private final RestTemplate restTemplate;
    private final String donacionesUrl;
    private final String apiToken;

    public DonacionesClient(RestTemplate restTemplate,
                            @Value("${donaciones.service.url}") String donacionesUrl,
                            @Value("${api.token}") String apiToken){
        this.restTemplate = restTemplate;
        this.donacionesUrl = donacionesUrl;
        this.apiToken = apiToken;
    }

    // PATCH /donacion/{id}/iniciarTraslado
    public void iniciarTraslado(Long donacionId, String responsableId) {
        cambiarEstado(donacionId, "iniciarTraslado", responsableId, null);
    }

    // PATCH /donacion/{id}/entregar con las URLs de las fotos que cargó la entidad
    // (donaciones guarda la primera para la landing)
    public void confirmarEntrega(Long donacionId, String responsableId, List<String> fotosUrl) {
        cambiarEstado(donacionId, "entregar", responsableId,
                Map.of("fotosUrl", fotosUrl != null ? fotosUrl : List.of()));
    }

    // PATCH /donacion/{id}/fallarEntrega (donaciones exige la justificación en el body)
    public void marcarEntregaFallida(Long donacionId, String motivo, String responsableId) {
        String motivoSeguro = motivo != null ? motivo : "Sin motivo especificado";
        cambiarEstado(donacionId, "fallarEntrega", responsableId, Map.of("justificacion", motivoSeguro));
    }

    // PATCH /donacion/{id}/listaParaEntregar (lo usa el planificador)
    public void marcarComoListaParaEntregar(Long donacionId) {
        cambiarEstado(donacionId, "listaParaEntregar", "SISTEMA_LOGISTICA", null);
    }

    // Obtiene la entidad beneficiaria con sus representantes y medios de
    // notificación, para poder notificarle los eventos de la entrega
    // GET /entidad/{id}
    public EntidadContactoDTO obtenerEntidad(Long entidadId) {
        return get("/entidad/{id}", EntidadContactoDTO.class, entidadId);
    }

    // Obtiene el donante humano con su medio de notificación predeterminado
    // GET /donante/humano/{id}
    public PersonaContactoDTO obtenerDonanteHumano(Long donanteId) {
        return get("/donante/humano/{id}", PersonaContactoDTO.class, donanteId);
    }

    // GET /donaciones?estado=ASIGNACION_REALIZADA
    public List<DonacionDTO> obtenerDonacionesAsignadas() {
        DonacionDTO[] response = get("/donaciones?estado={estado}", DonacionDTO[].class, "ASIGNACION_REALIZADA");
        return response != null ? Arrays.asList(response) : List.of();
    }

    private <T> T get(String ruta, Class<T> tipo, Object... variables) {
        return restTemplate.exchange(donacionesUrl + ruta, HttpMethod.GET,
                new HttpEntity<>(conApiToken()), tipo, variables).getBody();
    }

    private void cambiarEstado(Long donacionId, String accion, String responsableId, Object body) {
        restTemplate.exchange(
                donacionesUrl + "/donacion/{id}/{accion}?responsableId={responsable}",
                HttpMethod.PATCH,
                new HttpEntity<>(body, conApiToken()),
                Void.class,
                donacionId, accion, responsableId);
    }

    private HttpHeaders conApiToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_API_TOKEN, apiToken);
        return headers;
    }
}
