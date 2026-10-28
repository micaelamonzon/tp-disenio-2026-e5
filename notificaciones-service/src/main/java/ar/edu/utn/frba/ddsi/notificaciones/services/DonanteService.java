package ar.edu.utn.frba.ddsi.notificaciones.services;

import ar.edu.utn.frba.ddsi.notificaciones.dto.DonanteJuridicoDTO;
import ar.edu.utn.frba.ddsi.notificaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Donante;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.DonanteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

// Concentra la logica de resolver el donante contra donaciones-service y
// actualizar (upsert) su copia local. La usan tanto el listener (cola)
// como el controller (HTTP), para no duplicar el mismo codigo en los dos.
@Service
public class DonanteService {

    // URL base de donaciones-service para resolver el contacto del donante
    private static final String DONACIONES_SERVICE_URL =
            "http://localhost:8080/servicioDeDonaciones/donante/humano/";

    private final DonanteRepository donanteRepository;
    private final RestTemplate restTemplate;
    private final String apiToken;

    private static final String DONANTE_JURIDICO_URL =
            "http://localhost:8080/servicioDeDonaciones/donante/juridico/";

    // Donaciones solo deja leer el contacto de un donante al propio donante, al admin
    // o a un servicio: notificaciones se identifica con su API token
    public DonanteService(DonanteRepository donanteRepository, RestTemplate restTemplate,
                          @Value("${api.token}") String apiToken) {
        this.donanteRepository = donanteRepository;
        this.restTemplate = restTemplate;
        this.apiToken = apiToken;
    }

    private <T> T getDeDonaciones(String url, Class<T> tipo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Token", apiToken);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), tipo).getBody();
    }

    // Llama a donaciones-service para obtener los datos actuales del donante,
    // y pisa (upsert) la copia local en la tabla Donante con lo que devuelve
    // el GET: asi el envio siempre usa el contacto mas reciente, sin
    // necesitar un proceso de validacion periodico aparte.
    public Donante resolverYActualizarDonante(Long donanteId) {
        PersonaHumanaDTO persona = getDeDonaciones(DONACIONES_SERVICE_URL + donanteId, PersonaHumanaDTO.class);

        if (persona == null || persona.medioDeNotificacionPredeterminado() == null) {
            throw new IllegalStateException(
                    "No se pudo resolver el contacto del donante id=" + donanteId);
        }

        Donante donante = donanteRepository.findById(donanteId).orElse(new Donante());
        donante.setId(donanteId);
        donante.setNombre(persona.nombre());
        donante.setApellido(persona.apellido());
        donante.setTipoDeNotificacion(persona.medioDeNotificacionPredeterminado().tipoDeNotificacion());
        donante.setContacto(persona.medioDeNotificacionPredeterminado().datoDeContacto());
        donante.setFechaActualizacion(LocalDateTime.now());

        return donanteRepository.save(donante);
    }

    // los jurídicos no se guardan en la caché Donante (su id es de otra tabla
    // y chocaría con el de las personas humanas), solo se resuelve el contacto
    public DonanteJuridicoDTO.Medio resolverContactoJuridico(Long donanteJuridicoId) {
        DonanteJuridicoDTO juridico = getDeDonaciones(DONANTE_JURIDICO_URL + donanteJuridicoId, DonanteJuridicoDTO.class);

        if (juridico == null || juridico.mediosDeNotificacion() == null
                || juridico.mediosDeNotificacion().isEmpty()) {
            throw new IllegalStateException(
                    "No se pudo resolver el contacto del donante jurídico id=" + donanteJuridicoId);
        }
        return juridico.mediosDeNotificacion().get(0);
    }
}