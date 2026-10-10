package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroEntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroPersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroPersonaJuridicaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonacionesApiService {

    private static final Logger log = LoggerFactory.getLogger(DonacionesApiService.class);
    private final WebClient webClient = WebClient.builder().build();
    private final String donacionesServiceUrl;

    public DonacionesApiService(@Value("${donaciones.service.url}") String donacionesServiceUrl) {
        this.donacionesServiceUrl = donacionesServiceUrl;
    }


    public List<DonacionDTO> obtenerDonacionesDeHumano(Long idDonante) {
        try {
            List<DonacionDTO> donaciones = webClient.get()
                    .uri(donacionesServiceUrl + "/donaciones/humano/{id}", idDonante)
                    .retrieve()
                    .bodyToFlux(DonacionDTO.class)
                    .collectList()
                    .block();
            return donaciones != null ? donaciones : List.of();
        } catch (WebClientResponseException.NotFound e) {
            throw new DonanteNoEncontradoException(idDonante);
        } catch (Exception e) {
            log.error("Error llamando a donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las donaciones", e);
        }
    }

    // GET /servicioDeDonaciones/donaciones/juridico/{id}
    public List<DonacionDTO> obtenerDonacionesDeJuridico(Long idDonante) {
        try {
            List<DonacionDTO> donaciones = webClient.get()
                    .uri(donacionesServiceUrl + "/donaciones/juridico/{id}", idDonante)
                    .retrieve()
                    .bodyToFlux(DonacionDTO.class)
                    .collectList()
                    .block();
            return donaciones != null ? donaciones : List.of();
        } catch (WebClientResponseException.NotFound e) {
            throw new DonanteNoEncontradoException(idDonante);
        } catch (Exception e) {
            log.error("Error llamando a donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las donaciones", e);
        }
    }

    public static class DonanteNoEncontradoException extends RuntimeException {
        public DonanteNoEncontradoException(Long id) {
            super("No encontramos al donante " + id + ".");
        }
    }

    public List<EntidadBeneficiariaDTO> obtenerEntidades() {
        try {
            List<EntidadBeneficiariaDTO> entidades = webClient.get()
                    .uri(donacionesServiceUrl + "/obtenerEntidades")
                    .retrieve()
                    .bodyToFlux(EntidadBeneficiariaDTO.class)
                    .collectList()
                    .block();
            return entidades != null ? entidades : List.of();
        } catch (Exception e) {
            log.error("Error obteniendo entidades de donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las entidades", e);
        }
    }


    public EntidadBeneficiariaDTO obtenerEntidad(Long idEntidad) {
        try {
            return webClient.get()
                    .uri(donacionesServiceUrl + "/obtenerEntidad/{id}", idEntidad)
                    .retrieve()
                    .bodyToMono(EntidadBeneficiariaDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new EntidadNoEncontradaException(idEntidad);
        } catch (Exception e) {
            log.error("Error obteniendo la entidad {} de donaciones-service", idEntidad, e);
            throw new RuntimeException("No se pudo obtener la entidad", e);
        }
    }


    public List<NecesidadDTO> obtenerNecesidades(Long idEntidad) {
        try {
            List<NecesidadDTO> necesidades = webClient.get()
                    .uri(donacionesServiceUrl + "/entidad/{id}/necesidades", idEntidad)
                    .retrieve()
                    .bodyToFlux(NecesidadDTO.class)
                    .collectList()
                    .block();
            return necesidades != null ? necesidades : List.of();
        } catch (Exception e) {
            log.error("Error obteniendo necesidades de la entidad {}", idEntidad, e);
            throw new RuntimeException("No se pudieron obtener las necesidades", e);
        }
    }

    public static class EntidadNoEncontradaException extends RuntimeException {
        public EntidadNoEncontradaException(Long id) {
            super("No encontramos la entidad beneficiaria " + id + ".");
        }
    }


    public Long registrarDonanteHumano(RegistroPersonaHumanaDTO dto) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("nombre", dto.getNombre());
            body.put("apellido", dto.getApellido());
            body.put("edad", dto.getEdad());
            body.put("DNI", dto.getDNI());
            body.put("genero", dto.getGenero());

            Map<String, Object> medioEmail = medioEmail(dto.getEmail());
            body.put("mediosDeNotificacion", List.of(medioEmail));
            body.put("medioDeNotificacionPredeterminado", medioEmail);
            body.put("donaciones", List.of());

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/donante/humano")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extraerId(response);
        } catch (Exception e) {
            log.error("Error registrando donante humano en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar el donante humano", e);
        }
    }


    public Long registrarDonanteJuridico(RegistroPersonaJuridicaDTO dto) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("razonSocial", dto.getRazonSocial());
            body.put("cuit", dto.getCuit());
            body.put("rubro", dto.getRubro());

            if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
                body.put("tipo", dto.getTipo().toUpperCase().trim());
            } else {
                body.put("tipo", "EMPRESA"); // Colocar una constante por defecto si viene vacío
            }

            body.put("mediosDeNotificacion", List.of(medioEmail(dto.getEmail())));

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/donante/juridico")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extraerId(response);
        } catch (Exception e) {
            log.error("Error registrando donante jurídico en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la empresa o donante jurídico", e);
        }
    }

    public Long registrarEntidadBeneficiaria(RegistroEntidadBeneficiariaDTO dto) {
        try {
            Map<String, Object> representante = new HashMap<>();
            if (dto.getRepresentante() != null) {
                representante.put("nombre", dto.getRepresentante().getNombre());
                representante.put("apellido", dto.getRepresentante().getApellido());
                representante.put("numeroDeDocumento", dto.getRepresentante().getNumeroDeDocumento());
            }
            representante.put("mediosDeNotificacion", List.of(medioEmail(dto.getEmail())));

            Map<String, Object> body = new HashMap<>();
            body.put("razonSocial", dto.getRazonSocial());
            body.put("direccion", dto.getDireccion());
            body.put("telefono", dto.getTelefono());
            body.put("representantes", List.of(representante));

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/entidad")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extraerId(response);
        } catch (Exception e) {
            log.error("Error registrando entidad beneficiaria en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la entidad beneficiaria", e);
        }
    }

    // Bajas para deshacer un registro cuando falla la creación del usuario en autenticación
    public void eliminarDonanteHumano(Long id) {
        eliminar("/donante/humano/" + id);
    }

    public void eliminarDonanteJuridico(Long id) {
        eliminar("/donante/juridico/" + id);
    }

    public void eliminarEntidadBeneficiaria(Long id) {
        eliminar("/entidad/" + id);
    }

    private void eliminar(String path) {
        try {
            webClient.delete()
                    .uri(donacionesServiceUrl + path)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            // No se relanza: el error que importa mostrar es el del registro
            log.error("No se pudo deshacer el alta {} en donaciones-service", path, e);
        }
    }

    // Mismo formato que MedioDeNotificacionDTO de donaciones (tipoDeNotificacion, datoDeContacto)
    private Map<String, Object> medioEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return Map.of("tipoDeNotificacion", "EMAIL", "datoDeContacto", email.trim());
    }

    // Sin id no se puede vincular el usuario con su donante/entidad: se corta el registro
    private Long extraerId(Map response) {
        if (response == null || !(response.get("id") instanceof Number id)) {
            throw new IllegalStateException("donaciones-service no devolvió el id del alta");
        }
        return id.longValue();
    }

}
