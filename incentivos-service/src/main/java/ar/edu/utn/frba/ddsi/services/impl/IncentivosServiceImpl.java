package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.persona.*;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class IncentivosServiceImpl implements IncentivosService {

    private final IncentivosRepository incentivosRepository;
    private final RestTemplate restTemplate;
    private final RestProperties propiedades;
    private final InsigniaPublicadorService publicadorService;
    private RankingMensual ultimoRanking;

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

        incentivosRepository.guardarDonante(nuevoDonante);

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

        incentivosRepository.guardarDonante(nuevoDonante);
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
    public String publicarYDifundirInsignia(Long id, Insignia insignia) {

        // Buscás el donante en el repositorio en memoria
        Donante donante = incentivosRepository.findAllDonantes()
                .stream()
                .filter(d -> d.getId() != null && d.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (donante == null) return null;

        return publicadorService.publicarYDifundirInsignia(donante, insignia);
    }
/*
    @Override
    public void calcularYGuardarRanking() {

        // Traés todos los donantes del repositorio en memoria
        List<Donante> todosLosDonantes = incentivosRepository.findAllDonantes();

        if (todosLosDonantes.isEmpty()) return;

        List<Donante> ranking = todosLosDonantes.stream()
                .sorted(Comparator.comparingInt(
                        d -> -calcularMisionesCumplidasEnMesActual(d))
                )
                .limit(3)
                .toList();

        if (ranking.size() < 3) return;

        this.ultimoRanking = new RankingMensual(
                LocalDate.now().minusMonths(1),
                ranking.get(0),
                ranking.get(1),
                ranking.get(2)
        );
    } */
    @Override
    public RankingMensual obtenerUltimoRanking() {
        return ultimoRanking;
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