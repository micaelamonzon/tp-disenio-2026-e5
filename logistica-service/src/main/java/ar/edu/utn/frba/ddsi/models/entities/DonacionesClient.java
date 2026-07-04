package ar.edu.utn.frba.ddsi.models.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public void marcarEntregaFallida(Long donacionId, String motivo, String responsableId) {
        String url = donacionesUrl + "/servicioDeDonaciones/donacion/"
                + donacionId + "/fallarEntrega?responsableId=" + responsableId;
        restTemplate.patchForObject(url, null, String.class);
    }
}