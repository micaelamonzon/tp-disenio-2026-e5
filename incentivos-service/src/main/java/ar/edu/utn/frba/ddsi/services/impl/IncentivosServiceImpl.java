package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class IncentivosServiceImpl implements IncentivosService {

    private final IncentivosRepository incentivosRepository;
    private final RestTemplate restTemplate;
    private final RestProperties propiedades;

    public IncentivosServiceImpl(IncentivosRepository incentivosRepository, RestTemplate restTemplate, RestProperties propiedades) {
        this.incentivosRepository = incentivosRepository;
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
    }

    @Override
    public List<MisionDTO> buscarMisionesCompletadas(Long id){
        URI uri = UriComponentsBuilder.fromUriString(propiedades.getUrl()).path("/obtenerDonaciones/{id}")
                .buildAndExpand(id)
                .toUri();
        ResponseEntity<DonacionDTO[]> response = restTemplate.getForEntity(uri, DonacionDTO[].class);

        DonacionDTO[] arrayDeDonaciones = response.getBody();

        List<DonacionDTO> listaDeDonaciones =  arrayDeDonaciones == null || arrayDeDonaciones.length == 0 ? List.of() : Arrays.asList(arrayDeDonaciones);

      return  null;
    }

    @Override
    public List<InsigniaDTO> buscarInsigniasPorId(Long id){

        return null;
    }

    @Override
    public MisionDTO buscarMisionActualPorId(Long id){
        return null;
    }


}
