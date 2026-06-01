package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.N8NProperties;
import ar.edu.utn.frba.ddsi.dto.InsigniaWebhookRequestDTO;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.repositories.enMemoria.repositorioEnMemoria;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class InsigniaPublicadorService {
    private static final String PATH_INSIGNIA = "/insignia-obtenida-pt3";

    private final RestTemplate restTemplate;
    private final N8NProperties properties;
    IncentivosRepository incentivosRepository;

    public InsigniaPublicadorService(RestTemplate restTemplate, N8NProperties properties){
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public String publicarYDifundirInsignia(String nombreDonante, Insignia insignia) {
        URI uri = UriComponentsBuilder
                .fromUriString(properties.getBaseURL())
                .path(PATH_INSIGNIA)
                .build()
                .toUri();

        InsigniaWebhookRequestDTO request = new InsigniaWebhookRequestDTO(
                nombreDonante,
                insignia.getNombre()
        );

        String respuesta = restTemplate.postForObject(uri, request, String.class);
        return respuesta == null || respuesta.isBlank() ? null : respuesta;
    }
}
