package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeDonante;
import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
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
import java.time.YearMonth;
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

        Donante nuevoDonante = new Donante(personaDonante.id(),null,null, personaDonante.nombre(),personaDonante.apellido(),personaDonante.edad(),personaDonante.DNI(),personaDonante.genero(),personaDonante.direccion(),donaciones,misiones,new CategoriaDeDonante(personaDonante.categoria()));

        this.incentivosRepository.guardarDonante(nuevoDonante);

        nuevoDonante.getMisiones().forEach(m->nuevoDonante.getCategoria().agregarMision(m));

        List<Mision> misionesCompletadas = nuevoDonante.getCategoria().obtenerMisionesCompletadas(nuevoDonante.getDonaciones());
        List <MisionDTO> misionesCompletadasDTO = this.obtenerMisionDTO(misionesCompletadas);

        return  misionesCompletadasDTO;
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

        Donante nuevoDonante = new Donante(personaDonante.id(),personaDonante.cuit(),personaDonante.razonSocial(),null,null,null,null,null,null,donaciones,misiones,new CategoriaDeDonante(personaDonante.categoria()));

        this.incentivosRepository.guardarDonante(nuevoDonante);

        nuevoDonante.getMisiones().forEach(m->nuevoDonante.getCategoria().agregarMision(m));

        List<Mision> misionesCompletadas = nuevoDonante.getCategoria().obtenerMisionesCompletadas(nuevoDonante.getDonaciones());
        List <MisionDTO> misionesCompletadasDTO = this.obtenerMisionDTO(misionesCompletadas);

        return  misionesCompletadasDTO;
    }

    @Override
    public List<InsigniaDTO> buscarInsigniasPorId(Long id){
        Donante donante = this.incentivosRepository.buscarDonantePorId(id);
            if (donante == null){
                throw new RuntimeException("No se encontro el donante con id: " + id);
            }
            List<InsigniaDTO> insigniasDTO = this.obtenerInsigniasDTO(donante);

            return insigniasDTO;
    }

    @Override
    public MisionDTO buscarMisionActualPorId(Long id){
        Donante donante = this.incentivosRepository.buscarDonantePorId(id);
        if (donante == null){
            throw new RuntimeException("No se encontro el donante con id: " + id);
        }
            Mision misionActual = donante.getMisiones().stream().filter(m -> m.getEstadoDeMision() == EstadoDeMision.ACTUAL)
                    .findFirst()
                    .orElse(null);
                if (misionActual == null){
                    throw new RuntimeException("No se encontro una mision actual para el donante con id: " + id);
                }
            MisionDTO misionActualDTO = new MisionDTO(misionActual.getNombre(), misionActual.getEstadoDeMision(),misionActual.getFechaCompletada());

            return misionActualDTO;
    }

    @Override
    public String publicarYDifundirInsignia(Long id, Insignia insignia) {
        Donante donante = incentivosRepository.buscarDonantePorId(id);

        return publicadorService.publicarYDifundirInsignia(donante.getNombre(), insignia);
    }
    @Override
    public void calcularYGuardarRanking() {

        List<Donante> todosLosDonantes = incentivosRepository.findAllDonantes();

        YearMonth mesPasado = YearMonth.now().minusMonths(1);

        List<Donante> ranking = todosLosDonantes.stream()
                .sorted(Comparator.comparingInt(
                        d -> -d.calcularMisionesCumplidasEn(mesPasado))
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
    }
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
                            donacion.fechaDeIngreso(),
                            donacion.donacionEntregada(),
                            donacion.organizacionId()
                    );
                }).toList();

        return donaciones;
    }
    public List<Mision> convertirMisionesDTO(List<MisionDTO> misionesDTO){

        List<Mision> misiones = misionesDTO.stream().map(m -> new Mision(m.nombre())).toList();

        return misiones;
    }

    public List<MisionDTO> obtenerMisionDTO(List<Mision> misiones){
        return misiones.stream().map(m -> new MisionDTO(m.getNombre(), m.getEstadoDeMision(),m.getFechaCompletada())).toList();
    }
    public List<InsigniaDTO> obtenerInsigniasDTO(Donante donante){
        List<InsigniaDTO> insigniasDTO =  donante.getPerfil().getInsignias().stream().map(i -> new InsigniaDTO(i.getNombre(), i.texto())).toList();
        return insigniasDTO;
    }
    @Override
    public MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante) {

        Donante donante = (Donante) incentivosRepository.buscarPorId(idDonante)
                .orElseGet(() -> {
                    obtenerDonanteHumano(idDonante);
                    return incentivosRepository.buscarPorId(idDonante)
                            .orElseThrow(() -> new RuntimeException(
                                    "Donante no encontrado: " + idDonante));
                });

        String nombre = donante.getNombre() != null
                ? donante.getNombre() + " " + donante.getApellido()
                : donante.getRazonSocial();

        YearMonth mesPico = donante.mesDeMayorActividad();

        Integer posicion = (ultimoRanking != null)
                ? ultimoRanking.getPosicion(donante)
                : null;

        return new MetricasImpactoDTO(
                idDonante,
                nombre,
                donante.totalDonaciones(),
                mesPico,
                mesPico != null ? donante.cantidadDonacionesEnMes(mesPico) : 0,
                donante.calcularEvolucionMensual(),
                donante.compararConMesAnterior(),
                donante.totalOrganizacionesAyudadas(),
                posicion
        );
    }
    @Override
    public String procesarLogro(Long id, Insignia insignia, boolean esHumana) {
        URI uri = UriComponentsBuilder.fromUriString(propiedades.getUrl())
                .path("/servicioDeDonaciones/donantes/{id}") // endpoint de todos los donantes, tanto juridicos como humanos
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<PersonaDonanteDTO> response = restTemplate.getForEntity(uri, PersonaDonanteDTO.class);
        PersonaDonanteDTO dto = response.getBody();

        String nombre = esHumana ? dto.nombre() : dto.razonSocial();

        Donante donanteLocal = new Donante(
                id,
                null,
                nombre,
                null, null, null, null, null, null, // Atributos no necesarios aquí
                null,
                null,null
        );
        return publicadorService.publicarYDifundirInsignia(donanteLocal.getNombre(), insignia);
    }

}