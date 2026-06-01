package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.PersonaHumana;
import ar.edu.utn.frba.ddsi.models.entities.persona.Subcategoria;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.ResponseEntity;
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
    private final InsigniaPublicadorService publicadorService;

    public IncentivosServiceImpl(IncentivosRepository incentivosRepository, RestTemplate restTemplate, RestProperties propiedades, InsigniaPublicadorService publicadorService) {
        this.incentivosRepository = incentivosRepository;
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.publicadorService = publicadorService;
    }

    @Override
    public List<MisionDTO> obtenerDonanteHumano(Long id){

        URI uri = UriComponentsBuilder.fromUriString(propiedades.getUrl()).path("/humano/obtenerDonaciones/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<PersonaDonanteDTO> response = restTemplate.getForEntity(uri, PersonaDonanteDTO.class);

        PersonaDonanteDTO personaDonante = response.getBody();

        List<DonacionSinSegmentar> donaciones = this.convertirDonacionesDTO(personaDonante.donaciones());

        List<Mision> misiones = this.convertirMisionesDTO(personaDonante.misiones());

        Donante nuevoDonante = new Donante(null,null,personaDonante.nombre(),personaDonante.apellido(),personaDonante.edad(),personaDonante.DNI(),personaDonante.genero(),personaDonante.direccion(),donaciones,misiones);

        //guardar en el repo

        return  null;
    }
    @Override
    public List<MisionDTO> obtenerDonanteJuridico(Long id){

        URI uri = UriComponentsBuilder.fromUriString(propiedades.getUrl()).path("/juridico/obtenerDonaciones/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<PersonaDonanteDTO> response = restTemplate.getForEntity(uri, PersonaDonanteDTO.class);

        PersonaDonanteDTO personaDonante = response.getBody();

        List<DonacionSinSegmentar> donaciones = this.convertirDonacionesDTO(personaDonante.donaciones());

        List<Mision> misiones = this.convertirMisionesDTO(personaDonante.misiones());

        Donante nuevoDonante = new Donante(personaDonante.cuit(),personaDonante.razonSocial(),null,null,null,null,null,null,donaciones,misiones);

        //guardar en repo
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

    @Override
    public String publicarYDifundirInsignia(Long id, Insignia insignia){
        URI uri = UriComponentsBuilder
                .fromUriString(propiedades.getUrl())
                .path("/donante/{id}") // DEPENDE DEL ENDPOINT DE SERVICIO DE DONACIONES q todavia no esta, por ahi desp se debde cambiar
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<PersonaDonanteDTO> response = restTemplate.getForEntity(uri, PersonaDonanteDTO.class);
        PersonaDonanteDTO donante = response.getBody();

        if (donante == null) return null;
                // es el aviso a n8n
        return publicadorService.publicarYDifundirInsignia(donante.nombre(), insignia);
    }

    @Override
    public String procesarLogro(Long id, Insignia insignia, boolean esHumana) {
    String path = esHumana ? "/personas-humanas/" : "/personas-juridicas/"; // si es persona humana -> un path, sino el otro
    URI uri = UriComponentsBuilder.fromUriString(propiedades.getUrl())
            .path(path + "{id}").buildAndExpand(id).toUri();

    ResponseEntity<PersonaDonanteDTO> response = restTemplate.getForEntity(uri, PersonaDonanteDTO.class);
    PersonaDonanteDTO donante = response.getBody();

    String nombreAMencionar = esHumana ? donante.nombre() : donante.razonSocial();

    return publicadorService.publicarYDifundirInsignia(nombreAMencionar, insignia);
    }

    public List<DonacionSinSegmentar> convertirDonacionesDTO(List<DonacionSinSegmentarDTO> donacionesDTO){

        List<DonacionSinSegmentar> donaciones = donacionesDTO.stream().map(
                donacion -> {
                    List<Bien> bienes =
                            donacion.bienes().stream()
                                    .map(b -> new Bien(
                                            b.nombre(),
                                            b.descripcion(),
                                            null,
                                            new Subcategoria(
                                                    b.subcategoria().nombre(),
                                                    b.subcategoria().esPerecedero(),
                                                    new Categoria(
                                                            b.subcategoria().categoria().nombre()
                                                    )
                                            ),
                                            b.fechaDeVencimiento(),
                                            b.esUsado(),
                                            b.tipoUnidad(),
                                            b.cantidad()
                                    ))
                                    .toList();

                    return new DonacionSinSegmentar(
                            bienes,
                            donacion.fechaDeIngreso()
                    );
                }).toList();

        return donaciones;
    }
    public List<Mision> convertirMisionesDTO(List<MisionDTO> misionesDTO){

        List<Mision> misiones = misionesDTO.stream().map(m -> new Mision(m.nombre())).toList();

        return misiones;
    }
}