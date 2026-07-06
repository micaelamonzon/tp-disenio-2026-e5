package ar.edu.utn.frba.ddsi.models.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ar.edu.utn.frba.ddsi.dto.EntidadContactoDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaContactoDTO;

@Component
public class DonacionesClient {

    private final RestTemplate restTemplate;
    private final String donacionesUrl;

    public DonacionesClient(RestTemplate restTemplate,
                            @Value("${donaciones.service.url}") String donacionesUrl) {
        this.restTemplate = restTemplate;
        this.donacionesUrl = donacionesUrl;
    }

    public void iniciarTraslado(Long donacionId, String responsableId) {
        String url = donacionesUrl + "/servicioDeDonaciones/donacion/"
                + donacionId + "/iniciarTraslado?responsableId=" + responsableId;
        restTemplate.patchForObject(url, null, String.class);
    }

    public void confirmarEntrega(Long donacionId, String responsableId) {
        String url = donacionesUrl + "/servicioDeDonaciones/donacion/"
                + donacionId + "/entregar?responsableId=" + responsableId;
        restTemplate.patchForObject(url, null, String.class);
    }

   /* public void marcarEntregaFallida(Long donacionId, String motivo, String responsableId) {
        String url = donacionesUrl + "/servicioDeDonaciones/donacion/"
                + donacionId + "/fallarEntrega?responsableId=" + responsableId;
        restTemplate.patchForObject(url, null, String.class);
    } */

    public void marcarEntregaFallida(Long donacionId, String motivo, String responsableId) {
        String url = donacionesUrl + "/servicioDeDonaciones/donacion/"
                + donacionId + "/fallarEntrega?responsableId=" + responsableId;
        // El endpoint de donaciones espera la justificación en el body
        // (CambioEstadoRequestDTO con campo "justificacion")
        java.util.Map<String, String> body = java.util.Map.of("justificacion", motivo);
        restTemplate.patchForObject(url, body, String.class);
    }

    // Obtiene la entidad beneficiaria con sus representantes y medios de
    // notificación, para poder notificarle los eventos de la entrega
    public EntidadContactoDTO obtenerEntidad(Long entidadId) {
        String url = donacionesUrl + "/servicioDeDonaciones/obtenerEntidad/" + entidadId;
        return restTemplate.getForObject(url, EntidadContactoDTO.class);
    }

    // Obtiene el donante humano con su medio de notificación predeterminado
    public PersonaContactoDTO obtenerDonanteHumano(Long donanteId) {
        String url = donacionesUrl + "/servicioDeDonaciones/humano/" + donanteId;
        return restTemplate.getForObject(url, PersonaContactoDTO.class);
    }
}