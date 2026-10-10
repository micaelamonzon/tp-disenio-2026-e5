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

import java.util.ArrayList;
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

            List<Map<String, Object>> mediosNotificacion = new ArrayList<>();
            Map<String, Object> medioEmail = null;

            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                medioEmail = Map.of(
                        "tipoDeNotificacion", "EMAIL",
                        "contacto", dto.getEmail(),
                        "activo", true
                );
                mediosNotificacion.add(medioEmail);
            }

            body.put("mediosDeNotificacion", mediosNotificacion);
            body.put("medioDeNotificacionPredeterminado", medioEmail);
            body.put("donaciones", List.of());

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/donante/humano")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("id")) {
                return ((Number) response.get("id")).longValue();
            }

            return null;
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

            List<Map<String, Object>> mediosNotificacion = new ArrayList<>();
            Map<String, Object> medioEmail = null;

            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                medioEmail = Map.of(
                        "tipoDeNotificacion", "EMAIL",
                        "contacto", dto.getEmail(),
                        "activo", true
                );
                mediosNotificacion.add(medioEmail);
            }

            body.put("mediosDeNotificacion", mediosNotificacion);
            body.put("medioDeNotificacionPredeterminado", medioEmail);

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/donante/juridico")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("id")) {
                return ((Number) response.get("id")).longValue();
            }

            return null;
        } catch (Exception e) {
            log.error("Error registrando donante jurídico en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la empresa o donante jurídico", e);
        }
    }


    public Long registrarEntidadBeneficiaria(RegistroEntidadBeneficiariaDTO dto) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("razonSocial", dto.getRazonSocial());
            body.put("cuit", dto.getCuit());

            List<Map<String, Object>> mediosNotificacion = new ArrayList<>();
            Map<String, Object> medioEmail = null;

            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                medioEmail = Map.of(
                        "tipoDeNotificacion", "EMAIL",
                        "contacto", dto.getEmail(),
                        "activo", true
                );
                mediosNotificacion.add(medioEmail);
            }

            body.put("mediosDeNotificacion", mediosNotificacion);
            body.put("medioDeNotificacionPredeterminado", medioEmail);

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/crearEntidad")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("id")) {
                return ((Number) response.get("id")).longValue();
            }

            return null;
        } catch (Exception e) {
            log.error("Error registrando entidad beneficiaria en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la entidad beneficiaria", e);
        }
    }

}