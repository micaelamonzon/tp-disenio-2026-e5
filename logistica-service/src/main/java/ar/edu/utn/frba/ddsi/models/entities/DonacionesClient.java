package ar.edu.utn.frba.ddsi.models.entities;

import ar.edu.utn.frba.ddsi.dto.DonacionDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ar.edu.utn.frba.ddsi.dto.EntidadContactoDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaContactoDTO;

import java.util.Arrays;
import java.util.List;

@Component
public class DonacionesClient {

    private final RestTemplate restTemplate;
    private final String donacionesUrl;
    private final String BASE_URL = "http://localhost:8081/servicioDeDonaciones"; //TODO ver cual es el puerto

    public DonacionesClient(RestTemplate restTemplate,
                            @Value("${donaciones.service.url}") String donacionesUrl){
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
        String motivoSeguro = motivo != null ? motivo : "Sin motivo especificado";
        java.util.Map<String, String> body = java.util.Map.of("justificacion", motivoSeguro);
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

    public List<DonacionDTO> obtenerDonacionesAsignadas() {
        String url = BASE_URL + "/donaciones?estado=ASIGNACION_REALIZADA";
        DonacionDTO[] response = restTemplate.getForObject(url, DonacionDTO[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    public void marcarComoListaParaEntregar(Long donacionId) {
        String url = BASE_URL + "/donacion/" + donacionId + "/listaParaEntregar?responsableId=SISTEMA_LOGISTICA";
        restTemplate.exchange(url, HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
    }
}