package ar.edu.utn.frba.ddsi.notificaciones.services;

import ar.edu.utn.frba.ddsi.notificaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Donante;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.DonanteRepository;
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

    public DonanteService(DonanteRepository donanteRepository, RestTemplate restTemplate) {
        this.donanteRepository = donanteRepository;
        this.restTemplate = restTemplate;
    }

    // Llama a donaciones-service para obtener los datos actuales del donante,
    // y pisa (upsert) la copia local en la tabla Donante con lo que devuelve
    // el GET: asi el envio siempre usa el contacto mas reciente, sin
    // necesitar un proceso de validacion periodico aparte.
    public Donante resolverYActualizarDonante(Long donanteId) {
        PersonaHumanaDTO persona = restTemplate.getForObject(
                DONACIONES_SERVICE_URL + donanteId,
                PersonaHumanaDTO.class
        );

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
}