package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.config.N8NProperties;
import ar.edu.utn.frba.ddsi.dto.InsigniaWebhookRequestDTO;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.PersonaHumana;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class InsigniaPublicadorService {
    private static final String PATH_INSIGNIA = "/insignia-obtenida";

    private final RestTemplate restTemplate;
    private final N8NProperties properties;

    public InsigniaPublicadorService(RestTemplate restTemplate, N8NProperties properties){
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public String publicarYDifundirInsignia(PersonaHumana persona, Insignia insignia){
        URI uri = UriComponentsBuilder.
                fromUriString(properties.getBaseURL())
                .path(PATH_INSIGNIA)
                .build()
                .toUri();

        InsigniaWebhookRequestDTO request = new InsigniaWebhookRequestDTO(
                persona.getNombre(),
                insignia.getNombre(),
                insignia.texto()
        );

        String respuesta = restTemplate.postForObject(uri,request,String.class);

        return respuesta == null || respuesta.isBlank() ? null : respuesta;
    }
}
