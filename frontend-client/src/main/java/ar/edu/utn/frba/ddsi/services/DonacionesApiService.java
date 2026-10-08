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

import java.util.List;

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


    public void registrarDonanteHumano(RegistroPersonaHumanaDTO dto) {
        try {
            webClient.post()
                    .uri(donacionesServiceUrl + "/donantes/humana")
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error registrando donante humano en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar el donante humano", e);
        }
    }


    public void registrarDonanteJuridico(RegistroPersonaJuridicaDTO dto) {
        try {
            webClient.post()
                    .uri(donacionesServiceUrl + "/donantes/juridica")
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error registrando donante jurídico en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la empresa o donante jurídico", e);
        }
    }


    public void registrarEntidadBeneficiaria(RegistroEntidadBeneficiariaDTO dto) {
        try {
            webClient.post()
                    .uri(donacionesServiceUrl + "/entidades-beneficiarias")
                    .bodyValue(dto)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error registrando entidad beneficiaria en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la entidad beneficiaria", e);
        }
    }

}