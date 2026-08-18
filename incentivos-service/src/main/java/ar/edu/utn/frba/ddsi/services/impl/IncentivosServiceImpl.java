package ar.edu.utn.frba.ddsi.services.impl;
import ar.edu.utn.frba.ddsi.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.exceptions.ResourceNotFoundException;
import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Racha;
import ar.edu.utn.frba.ddsi.models.entities.persona.*;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class IncentivosServiceImpl implements IncentivosService {

    private final IncentivosRepository incentivosRepository;
    private  final RestTemplate restTemplate;
    private  final RestProperties propiedades;
    private final InsigniaPublicadorService publicadorService;
    private RankingMensual ultimoRanking;
    private List<Donante> rankingCompletoOrdenado = new ArrayList<>();
    private final NotificacionesProperties notificacionesProperties;

    public IncentivosServiceImpl(IncentivosRepository incentivosRepository, RestTemplate restTemplate, RestProperties propiedades, InsigniaPublicadorService publicadorService, NotificacionesProperties notificacionesProperties) {
        this.incentivosRepository = incentivosRepository;
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.publicadorService = publicadorService;
        this.notificacionesProperties = notificacionesProperties;
    }

    @Override
    public void agregarMisionADonante(Long id, MisionDTO misionDTO){
        //Yo como donante, elijo una mision -> el servis de incentivos me toma el id y la mision, me guarda en su repo,
        // si no está mi id, entonces me instancia como un nuevo donante -> luego llama al servis de donaciones mandandole mi id y mi tipo (humana o juridica), para traerse las donaciones de ese id
       Mision mision  = this.convertirMisionDTO(misionDTO);
       Donante donante = incentivosRepository.buscarDonantePorId(id);
       if(donante != null){
           donante.agregarMision(mision);
       }
    }

    public void pedirDonantesAServiceDonaciones(){

        URI uri = UriComponentsBuilder
                .fromUriString(propiedades.getUrl())
                .path("/obtenerDonantes")
                .build()
                .toUri();

        ResponseEntity<PersonaDonanteDTO[]> response = restTemplate.getForEntity(uri, PersonaDonanteDTO[].class);
        PersonaDonanteDTO[] arrayDeDonantes = response.getBody();

        if (arrayDeDonantes == null || arrayDeDonantes.length == 0){
            throw new RuntimeException("No se pudo traer a los donantes del servicio de donaciones");
        }


        for (PersonaDonanteDTO personaDonante : arrayDeDonantes) {

            List<DonacionSinSegmentar> donaciones = this.convertirDonacionesDTO(personaDonante.donaciones());

            MedioDeNotificacion medioDeNotificacionPredeterminado = this.convertirMedioDeNotificacionDTO(personaDonante.medioDeNotificacionPredeterminado());

            if(personaDonante.idHumano() != null){
                Donante nuevoDonante = new Donante(
                                        null,
                                        personaDonante.nombre(),
                                        personaDonante.apellido(),
                                        null,
                                        donaciones,
                                        null,
                                        medioDeNotificacionPredeterminado,
                                        TipoDeDonante.HUMANO);

                this.incentivosRepository.guardarDonante(nuevoDonante);

                String nombreDeUsuario = nuevoDonante.getNombre() + " " + nuevoDonante.getApellido();
                nuevoDonante.setPerfil(new Perfil(nombreDeUsuario,null));

            }else if(personaDonante.idJuridico() != null){
                Donante nuevoDonante = new Donante(
                        null,
                        null,
                        null,
                        personaDonante.razonSocial(),
                        donaciones,
                        null,
                        medioDeNotificacionPredeterminado,
                        TipoDeDonante.JURIDICO);

                this.incentivosRepository.guardarDonante(nuevoDonante);

                String nombreDeUsuario = nuevoDonante.getRazonSocial();
                nuevoDonante.setPerfil(new Perfil(nombreDeUsuario,null));
            }
        }
    }

    @Override
    public List<MisionDTO> obtenerMisionesCompletadas(Long id) {

        Donante donante = this.incentivosRepository.buscarDonantePorId(id);

        if(donante == null){
            throw new RuntimeException("No se encontro el donante con id: " + id);
        }

        List<Mision> misionesCompletadas = donante.getMisiones();

        // Evento 4: Notificar al donante que cumplió una misión
        if (misionesCompletadas != null && !misionesCompletadas.isEmpty()) {
            String nombreMision = misionesCompletadas.get(0).getNombre();
            // Usamos el email del donante obtenido del servicio de donaciones
            if (donante.getMedioDeNotificacionPredeterminado() != null) {
                notificar(
                        donante.getMedioDeNotificacionPredeterminado().getDatoDeContacto(),
                        "Felicitaciones!+Completaste+la+mision+" + nombreMision,
                        donante.getMedioDeNotificacionPredeterminado().getDatoDeContacto()
                );
            }
        }

        // Evento 5: Notificar al donante que cambió de categoría
        if (donante.getMisionEnCurso().pasaSiguienteCategoria(donante.getDonaciones())) {
            if (donante.getMedioDeNotificacionPredeterminado() != null) {
                String nuevaCategoria = donante.getMisionEnCurso().getCategoriaActual().toString();
                notificar(
                        donante.getMedioDeNotificacionPredeterminado().getDatoDeContacto(),
                        "Subiste+de+categoria!+Ahora+sos+donante+" + nuevaCategoria,
                        donante.getMedioDeNotificacionPredeterminado().getTipoDeNotificacion()
                );
            }
        }

        List <MisionDTO> misionesCompletadasDTO = this.obtenerMisionDTO(misionesCompletadas);

        return misionesCompletadasDTO;
    }

    public void agregarInsignias(Donante nuevoDonante, List<Mision> misionesCompletadas){
        if(misionesCompletadas != null && !misionesCompletadas.isEmpty()){
            misionesCompletadas.forEach(m->{nuevoDonante.getPerfil().agregarInsignia(m.getInsigniaGanadora());});
        }
    }



    @Override
    public List<InsigniaDTO> buscarInsigniasPorId(Long id) {
        Donante donante = this.incentivosRepository.buscarDonantePorId(id);
        if (donante == null) {
            throw new RuntimeException("No se encontro el donante con id: " + id);
        }
        List<InsigniaDTO> insigniasDTO = this.obtenerInsigniasDTO(donante);

        return insigniasDTO;
    }

    @Override
    public MisionDTO buscarMisionActualPorId(Long id) {
        Donante donante = this.incentivosRepository.buscarDonantePorId(id);
        if (donante == null) {
            throw new RuntimeException("No se encontro el donante con id: " + id);
        }

        MisionDTO misionActualDTO = new MisionDTO(donante.getMisionEnCurso().getMisionActual().getNombre(),
                                                    donante.getMisionEnCurso().getMisionActual().getEstadoDeMision(),
                                                    donante.getMisionEnCurso().getCategoriaActual());
        return misionActualDTO;
    }

    @Override
    public String publicarYDifundirInsignia(Long id, Insignia insignia) {
        Donante donante = incentivosRepository.buscarDonantePorId(id);
        if (donante == null) {
            System.out.println("Donante " + id + " no encontrado localmente. Sincronizando...");

            try {
                URI uri = UriComponentsBuilder
                        .fromUriString("http://localhost:8080/servicioDeDonaciones")
                        .path("/humano/{id}")
                        .buildAndExpand(id)
                        .toUri();

                Donante donanteImportado = restTemplate.getForObject(uri, Donante.class);

                if (donanteImportado != null) {
                    incentivosRepository.guardarDonante(donanteImportado);
                    donante = donanteImportado;
                    System.out.println("Sincronización exitosa: " + donante.getNombre());
                } else {
                    throw new ResourceNotFoundException("Donante no existe en el sistema integral: " + id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResourceNotFoundException("No se pudo recuperar al donante del servicio externo: " + id);
            }
        }
        String nombre = donante.getNombre();
        return publicadorService.publicarYDifundirInsignia(nombre, insignia);
    }

    @Override
    public void calcularYGuardarRanking() {
        URI uri = UriComponentsBuilder
                .fromUriString(propiedades.getUrl())
                .path("/obtenerDonantes")
                .build()
                .toUri();

        ResponseEntity<PersonaDonanteDTO[]> response = restTemplate.getForEntity(uri, PersonaDonanteDTO[].class);
        PersonaDonanteDTO[] array = response.getBody();
        if (array == null || array.length == 0) return;

        YearMonth mesPasado = YearMonth.now().minusMonths(1);

        List<Donante> donantes = Arrays.stream(array).map(dto -> {

            List<DonacionSinSegmentar> donaciones = this.convertirDonacionesDTO(dto.donaciones());

            MedioDeNotificacion medioDeNotificacionPredeterminado = this.convertirMedioDeNotificacionDTO(dto.medioDeNotificacionPredeterminado());

            if(dto.idHumano() != null){

                Donante nuevoDonante = new Donante(
                        null,
                        dto.nombre(),
                        dto.apellido(),
                        null,
                        donaciones,
                       null,
                        medioDeNotificacionPredeterminado,
                        TipoDeDonante.HUMANO);

                return nuevoDonante;

            }else if(dto.idJuridico() != null){
                Donante nuevoDonante = new Donante(
                        null,
                        null,
                        null,
                        dto.razonSocial(),
                        donaciones,
                        null,
                        medioDeNotificacionPredeterminado,
                        TipoDeDonante.JURIDICO);

                return nuevoDonante;
            }

            return null;
        }).toList();

        this.rankingCompletoOrdenado = donantes.stream()
                .sorted(Comparator.comparingInt(
                        (Donante d) -> -d.calcularMisionesCumplidasEn(mesPasado)))
                .toList();

        this.ultimoRanking = new RankingMensual(
                LocalDate.now().minusMonths(1),
                rankingCompletoOrdenado.get(0),
                rankingCompletoOrdenado.get(1),
                rankingCompletoOrdenado.get(2),
                rankingCompletoOrdenado
        );
    }

    @Override
    public RankingMensual obtenerUltimoRanking() {
        return ultimoRanking;
    }


    public List<DonacionSinSegmentar> convertirDonacionesDTO(List<DonacionSinSegmentarDTO> donacionesDTO) {

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

    public List<Mision> convertirMisionesDTO(List<MisionDTO> misionesDTO) {

        List<Mision> misiones = misionesDTO.stream().map(m -> new Mision(m.nombre(), m.estado())).toList();

        return misiones;
    }

    public List<MisionDTO> obtenerMisionDTO(List<Mision> misiones) {
        List<MisionDTO> misionesDTO = new ArrayList<>();
        misiones.forEach(m -> misionesDTO.add(new MisionDTO(m.getNombre(), m.getEstadoDeMision(), m.getCategoria())));
        return misionesDTO;
    }

    public List<InsigniaDTO> obtenerInsigniasDTO(Donante donante) {
        List<InsigniaDTO> insigniasDTO = donante.getPerfil().getInsignias().stream().map(i -> new InsigniaDTO(i.getNombre(), i.texto())).toList();
        return insigniasDTO;
    }

    public Mision convertirMisionDTO(MisionDTO misionDTO){
        Mision mision = new Mision(misionDTO.nombre(), misionDTO.estado());
        return mision;
    }

    public MedioDeNotificacion convertirMedioDeNotificacionDTO(MedioDeNotificacionDTO medioDeNotificacionPredeterminadoDTO){

        MedioDeNotificacion medioDeNotificacionPredeterminado = new MedioDeNotificacion(medioDeNotificacionPredeterminadoDTO.tipoDeNotificacion(), medioDeNotificacionPredeterminadoDTO.datoDeContacto());

        return medioDeNotificacionPredeterminado;
    }

    @Override
    public MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante) {

        Donante donante = incentivosRepository.buscarDonantePorId(idDonante);
//
//        if (donante == null) {
//            obtenerDonanteHumano(idDonante);
//            donante = incentivosRepository.buscarDonantePorId(idDonante);
//        }

        if (donante == null) {
            throw new RuntimeException("Donante no encontrado: " + idDonante);
        }

        String nombre = donante.getNombre() != null
                ? donante.getNombre() + " " + donante.getApellido()
                : donante.getRazonSocial();

        YearMonth mesPico = donante.mesDeMayorActividad();

        if (ultimoRanking == null) {
            calcularYGuardarRanking();
        }

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
        Donante donante = incentivosRepository.buscarDonantePorId(id);
//        if (donante == null) {
//            if (esHumana) {
//                obtenerDonanteHumano(id);
//            } else {
//                obtenerDonanteJuridico(id);
//            }
//            donante = incentivosRepository.buscarDonantePorId(id);
//        }

        String nombre = donante.getNombre() != null
                ? donante.getNombre() + " " + donante.getApellido()
                : donante.getRazonSocial();
        return publicadorService.publicarYDifundirInsignia(nombre, insignia);
    }

    @Override
    public MetricasSistemaDTO obtenerMetricasDelSistema() {
        List<Donante> todos = incentivosRepository.findAllDonantes();
        YearMonth mesActual = YearMonth.now();
        YearMonth mesAnterior = mesActual.minusMonths(1);

        // Donantes activos (que tienen al menos una donación)
        int totalActivos = (int) todos.stream()
                .filter(d -> d.getDonaciones() != null
                        && !d.getDonaciones().isEmpty())
                .count();

        // Donaciones de este mes en toda la plataforma
        int donacionesEsteMes = todos.stream()
                .filter(d -> d.getDonaciones() != null)
                .flatMap(d -> d.getDonaciones().stream())
                .filter(don -> YearMonth.from(
                        don.getFechaDeIngreso().toLocalDate()).equals(mesActual))
                .mapToInt(d -> 1)
                .sum();

        // Donaciones del mes anterior para comparar
        int donacionesMesAnterior = todos.stream()
                .filter(d -> d.getDonaciones() != null)
                .flatMap(d -> d.getDonaciones().stream())
                .filter(don -> YearMonth.from(
                        don.getFechaDeIngreso().toLocalDate()).equals(mesAnterior))
                .mapToInt(d -> 1)
                .sum();

        // Donaciones entregadas este mes
        int entregadasEsteMes = todos.stream()
                .filter(d -> d.getDonaciones() != null)
                .flatMap(d -> d.getDonaciones().stream())
                .filter(don -> YearMonth.from(
                        don.getFechaDeIngreso().toLocalDate()).equals(mesActual))
                .filter(don -> Boolean.TRUE.equals(don.getDonacionEntregada()))
                .mapToInt(d -> 1)
                .sum();

        // Misiones completadas este mes en toda la plataforma
        int misionesEsteMes = todos.stream()
                .mapToInt(d -> d.calcularMisionesCumplidasEn(mesActual))
                .sum();

        // Top 3 del ranking
        List<String> top3 = new ArrayList<>();
        if (ultimoRanking != null) {
            if (ultimoRanking.getPrimerPuesto() != null)
                top3.add(ultimoRanking.getPrimerPuesto().getNombre());
            if (ultimoRanking.getSegundoPuesto() != null)
                top3.add(ultimoRanking.getSegundoPuesto().getNombre());
            if (ultimoRanking.getTercerPuesto() != null)
                top3.add(ultimoRanking.getTercerPuesto().getNombre());
        }
        int donantesNuevosEsteMes = (int) todos.stream()
                .filter(d -> d.esNuevoEn(mesActual))
                .count();

        return new MetricasSistemaDTO(
                totalActivos,
                donantesNuevosEsteMes,
                donacionesEsteMes,
                entregadasEsteMes,
                donacionesEsteMes - entregadasEsteMes,
                donacionesEsteMes - donacionesMesAnterior,
                misionesEsteMes,
                top3
        );
    }

    @Override
    public RankingMensualDTO obtenerUltimoRankingDTO() {
        RankingMensual ranking = this.obtenerUltimoRanking();

        if (ranking == null) return null;

        return mapearDto(ranking);
    }

    private RankingMensualDTO mapearDto(RankingMensual ranking) {
        return new RankingMensualDTO(
                ranking.getFecha(),
                ranking.getPrimerPuesto() != null ? ranking.getPrimerPuesto().getNombre() : "N/A",
                ranking.getSegundoPuesto() != null ? ranking.getSegundoPuesto().getNombre() : "N/A",
                ranking.getTercerPuesto() != null ? ranking.getTercerPuesto().getNombre() : "N/A"
        );
    }
    @Override
    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio) {
        List<RankingMensual> todosLosRankings = incentivosRepository.findAllRankings();

        return todosLosRankings.stream()
                .filter(r -> (anio == null || r.getFecha().getYear() == anio))
                .filter(r -> (mes == null || r.getFecha().getMonthValue() == mes))
                .map(this::mapearDto)
                .toList();
    }

    // Método para llamar al servicio de notificaciones
    // URL resultante: http://localhost:8081/servicioDeNotificaciones/notificar?destinatario=X&mensaje=Y&medio=Z
    private void notificar(String destinatario, String mensaje, String medio) {
        try {
            String url = notificacionesProperties.getUrl() +
                    "/servicioDeNotificaciones/notificar" +
                    "?destinatario=" + URLEncoder.encode(destinatario, StandardCharsets.UTF_8) +
                    "&mensaje=" + mensaje +
                    "&medio=" + medio;
            restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            System.out.println("Error al notificar: " + e.getMessage());
        }
    }
}