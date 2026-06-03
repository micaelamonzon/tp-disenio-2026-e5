package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeDonante;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Completitud;
import ar.edu.utn.frba.ddsi.models.entities.misiones.DonacionesExitosas;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.HabilDonador;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Racha;
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

        Donante nuevoDonante = new Donante(null,null,personaDonante.nombre(),personaDonante.apellido(),personaDonante.edad(),personaDonante.DNI(),personaDonante.genero(),personaDonante.direccion(),donaciones,misiones,new CategoriaDeDonante(personaDonante.categoria()));

        this.incentivosRepository.guardarDonante(nuevoDonante);

        nuevoDonante.setPerfil(new Perfil(nuevoDonante.getNombre(),nuevoDonante.getCategoria()));
        //defino el tipo de mision que es
        List<Mision> misionesDefinidas = this.definirMisiones(misiones);

        if(misionesDefinidas!=null){
            misionesDefinidas.forEach(m->nuevoDonante.getCategoria().agregarMision(m));
        }else{
            throw new RuntimeException("No hay misiones definidas para esta persona");
        }

        List<Mision> misionesCompletadas = nuevoDonante.getCategoria().obtenerMisionesCompletadas(nuevoDonante.getDonaciones());
        this.agregarInsignias(nuevoDonante, misionesCompletadas);
        List <MisionDTO> misionesCompletadasDTO = this.obtenerMisionDTO(misionesCompletadas);

        return  misionesCompletadasDTO;
    }
    public void agregarInsignias(Donante nuevoDonante, List<Mision> misionesCompletadas){
        if(!misionesCompletadas.isEmpty() && misionesCompletadas!= null){
            misionesCompletadas.forEach(m->{nuevoDonante.getPerfil().agregarInsignia(m.getInsigniaGanadora());});
        }
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

        Donante nuevoDonante = new Donante(personaDonante.cuit(),personaDonante.razonSocial(),null,null,null,null,null,null,donaciones,misiones,new CategoriaDeDonante(personaDonante.categoria()));

        this.incentivosRepository.guardarDonante(nuevoDonante);

        nuevoDonante.setPerfil(new Perfil(nuevoDonante.getNombre(),nuevoDonante.getCategoria()));
        //defino el tipo de mision que es
        List<Mision> misionesDefinidas = this.definirMisiones(misiones);
            if(misionesDefinidas!=null){
                misionesDefinidas.forEach(m->nuevoDonante.getCategoria().agregarMision(m));
            }else{
                throw new RuntimeException("No hay misiones definidas para esta persona");
            }

        List<Mision> misionesCompletadas = nuevoDonante.getCategoria().obtenerMisionesCompletadas(nuevoDonante.getDonaciones());
        List <MisionDTO> misionesCompletadasDTO = this.obtenerMisionDTO(misionesCompletadas);
        this.agregarInsignias(nuevoDonante, misionesCompletadas);
        return  misionesCompletadasDTO;
    }

    List<Mision> definirMisiones( List<Mision>  misionesSinDefinir){
        return misionesSinDefinir.stream().map(m -> {
            if(m.getNombre().equals("Racha")){
                return new Racha(m.getNombre(),2, 2, 3);
            } else if (m.getNombre().equals("HabilDonador")) {
                return new HabilDonador(m.getNombre(), 10);
            } else if (m.getNombre().equals("DonacionesExitosas")) {
                return new DonacionesExitosas(m.getNombre(), 2);
            } else  if (m.getNombre().equals("Completitud")) {
                return new Completitud(m.getNombre(), List.of(new Categoria("Alimentos"), new Categoria("Ropa"), new Categoria("Juguetes")));
            }
            return null;
        }).toList();

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
            MisionDTO misionActualDTO = new MisionDTO(misionActual.getNombre(), misionActual.getEstadoDeMision());

            return misionActualDTO;
    }

    @Override
    public String publicarYDifundirInsignia(Long id, Insignia insignia) {

        Donante donante = incentivosRepository.findAllDonantes()
                .stream()
                .filter(d -> d.getId() != null && d.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (donante == null) return null;

        return publicadorService.publicarYDifundirInsignia(donante, insignia);
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
                            donacion.donacionEntregada()
                    );
                }).toList();

        return donaciones;
    }
    public List<Mision> convertirMisionesDTO(List<MisionDTO> misionesDTO){

        List<Mision> misiones = misionesDTO.stream().map(m -> new Mision(m.nombre(), m.estadoDeMision())).toList();

        return misiones;
    }

    public List<MisionDTO> obtenerMisionDTO(List<Mision> misiones){
        return misiones.stream().map(m -> new MisionDTO(m.getNombre(), m.getEstadoDeMision())).toList();
    }
    public List<InsigniaDTO> obtenerInsigniasDTO(Donante donante){
        List<InsigniaDTO> insigniasDTO =  donante.getPerfil().getInsignias().stream().map(i -> new InsigniaDTO(i.getNombre(), i.texto())).toList();
        return insigniasDTO;
    }
}