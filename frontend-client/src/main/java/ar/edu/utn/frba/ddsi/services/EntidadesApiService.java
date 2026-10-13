package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.entidades.DonacionAsignadaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadDTO;
import ar.edu.utn.frba.ddsi.services.internal.WebApiCallerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EntidadesApiService {

    private final WebApiCallerService apiCaller;
    private final String baseUrl;

    public EntidadesApiService(@Value("${donaciones.service.url}") String baseUrl,
                               WebApiCallerService apiCaller) {
        this.apiCaller = apiCaller;
        this.baseUrl = baseUrl;
    }

    public NecesidadDTO crearNecesidad(Long idEntidad, NecesidadDTO body) {
        return apiCaller.post(baseUrl + "/entidad/{idEntidad}/necesidad", body, NecesidadDTO.class, idEntidad);
    }

    public List<String> obtenerSubcategorias() {
        // Se pide como String[]: con getList(String.class) WebClient devuelve todo el JSON
        // como un único String en vez de separar los elementos del array
        String[] subcategorias = apiCaller.get(baseUrl + "/subcategorias/todas", String[].class);
        return subcategorias != null ? List.of(subcategorias) : List.of();
    }

    public EntidadBeneficiariaDTO obtenerPerfil(Long idEntidad) {
        return apiCaller.get(baseUrl + "/entidad/{id}", EntidadBeneficiariaDTO.class, idEntidad);
    }

    public List<DonacionAsignadaDTO> obtenerDonacionesAsignadas(Long idEntidad) {
        return apiCaller.getList(baseUrl + "/entidadBeneficiaria/{idEntidad}/donaciones", DonacionAsignadaDTO.class, idEntidad);
    }
}
