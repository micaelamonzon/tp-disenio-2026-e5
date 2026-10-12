package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.PerfilDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.DonacionAsignadaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.PerfilEntidadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class EntidadesApiService {

    private final WebClient webClient;
    private final String baseUrl;

    public EntidadesApiService(@Value("${donaciones.service.url}") String baseUrl) {
        this.webClient =  WebClient.builder().build();;
        this.baseUrl=baseUrl;
    }

    public NecesidadDTO crearNecesidad(Long idEntidad, NecesidadDTO body) {
        return webClient.post()
                .uri(baseUrl + "/entidad/{idEntidad}/necesidad", idEntidad)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(NecesidadDTO.class)
                .block();
    }

    public List<String> obtenerSubcategorias() {
        return webClient.get()
                .uri(baseUrl + "/subcategorias/todas")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    public EntidadBeneficiariaDTO obtenerPerfil(Long idEntidad) {
        return webClient.get()
                .uri(baseUrl + "/entidad/{id}", idEntidad)
                .retrieve()
                .bodyToMono(EntidadBeneficiariaDTO.class)
                .block();
    }

    public List<DonacionAsignadaDTO> obtenerDonacionesAsignadas(Long idEntidad) {
        return webClient.get()
                .uri(baseUrl + "/entidadBeneficiaria/{idEntidad}/donaciones", idEntidad)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DonacionAsignadaDTO>>() {})
                .block();
    }
}
