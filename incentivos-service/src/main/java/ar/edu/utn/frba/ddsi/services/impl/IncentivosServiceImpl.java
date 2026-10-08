package ar.edu.utn.frba.ddsi.services.impl;
import ar.edu.utn.frba.ddsi.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.TipoCategoria;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.misiones.AvanceDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Completitud;
import ar.edu.utn.frba.ddsi.models.entities.misiones.DonacionesExitosas;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.HabilDonador;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import ar.edu.utn.frba.ddsi.models.entities.misiones.NombreDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Racha;
import ar.edu.utn.frba.ddsi.models.entities.persona.*;
import ar.edu.utn.frba.ddsi.repositories.CategoriaDeMisionRepository;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.repositories.MisionParticularRepository;
import ar.edu.utn.frba.ddsi.repositories.MisionRepository;
import ar.edu.utn.frba.ddsi.repositories.RankingMensualRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IncentivosServiceImpl implements IncentivosService {

    private final IncentivosRepository incentivosRepository;
    private final RestTemplate restTemplate;
    private final RestProperties propiedades;
    private final InsigniaPublicadorService publicadorService;
    private RankingMensual ultimoRanking;
    private List<Donante> rankingCompletoOrdenado = new ArrayList<>();
    private final NotificacionesProperties notificacionesProperties;
    private final RankingMensualRepository rankingMensualRepository;
    private final MisionRepository misionRepository;
    private final CategoriaDeMisionRepository categoriaDeMisionRepository;
    private final MisionParticularRepository misionParticularRepository;

    public IncentivosServiceImpl(IncentivosRepository incentivosRepository, RestTemplate restTemplate, RestProperties propiedades,
                                 InsigniaPublicadorService publicadorService, NotificacionesProperties notificacionesProperties,
                                 RankingMensualRepository rankingMensualRepository, MisionRepository misionRepository,
                                 CategoriaDeMisionRepository categoriaDeMisionRepository, MisionParticularRepository misionParticularRepository) {
        this.incentivosRepository = incentivosRepository;
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.publicadorService = publicadorService;
        this.notificacionesProperties = notificacionesProperties;
        this.rankingMensualRepository = rankingMensualRepository;
        this.misionRepository = misionRepository;
        this.categoriaDeMisionRepository = categoriaDeMisionRepository;
        this.misionParticularRepository = misionParticularRepository;
    }

    public void pedirDonantesAServiceDonaciones(){

        URI uri = UriComponentsBuilder
                .fromUriString(propiedades.getUrl())
                .path("/donantes")
                .build()
                .toUri();

        ResponseEntity<PersonaDonanteDTO[]> response = restTemplate.getForEntity(uri, PersonaDonanteDTO[].class);
        PersonaDonanteDTO[] arrayDeDonantes = response.getBody();

        if (arrayDeDonantes == null || arrayDeDonantes.length == 0){
            throw new RuntimeException("No se pudo traer a los donantes del servicio de donaciones");
        }

        for (PersonaDonanteDTO personaDonante : arrayDeDonantes) {

          List<DonacionSegmentada> donaciones = this.convertirDonacionesDTO(personaDonante.donaciones());

            if(personaDonante.idHumano() != null){
                Donante nuevoDonante = new Donante(
                        personaDonante.idHumano(),
                        personaDonante.nombre(),
                        personaDonante.apellido(),
                        null,
                        personaDonante.fechaDeRegistro(),
                        donaciones,
                        TipoDeDonante.HUMANO);

                this.incentivosRepository.save(nuevoDonante);

                String nombreDeUsuario = nuevoDonante.getNombre() + " " + nuevoDonante.getApellido();
                nuevoDonante.setPerfil(new Perfil(nombreDeUsuario,null));

            }else if(personaDonante.idJuridico() != null){
                Donante nuevoDonante = new Donante(
                        personaDonante.idJuridico(),
                        null,
                        null,
                        personaDonante.razonSocial(),
                        personaDonante.fechaDeRegistro(),
                        donaciones,
                        TipoDeDonante.JURIDICO);

                this.incentivosRepository.save(nuevoDonante);

                String nombreDeUsuario = nuevoDonante.getRazonSocial();
                nuevoDonante.setPerfil(new Perfil(nombreDeUsuario,null));

            }
        }

        System.out.println("La cantidad de donantes en el service de incentivos es " + this.incentivosRepository.findAll().size());


    }

    @Override
    @Transactional
    public void agregarMisionADonante(Long id, TipoDeDonante tipoDeDonante, Long misionId){

       Mision mision  = this.misionRepository.findById(misionId).orElseThrow(() -> new RuntimeException("Misión no encontrada"));

       Donante donante = incentivosRepository.findByIdEnDonaciones(id,tipoDeDonante);

       MisionParticular misionParticular = new MisionParticular(mision, donante);
        misionParticularRepository.save(misionParticular);

       if(donante != null){
           donante.agregarMision(misionParticular);
           donante.setMisionEnCurso(misionParticular);
           return;
       }
        throw new RuntimeException("No se encontro el donante con id: " + id + " tipo " + tipoDeDonante);

    }

    @Override
    public List<MisionParticularDTO> obtenerMisionesCompletadas(Long id,TipoDeDonante tipoDeDonante) {

        Donante donante = this.incentivosRepository.findByIdEnDonaciones(id,tipoDeDonante);

        if(donante == null){
            throw new RuntimeException("No se encontro el donante con id: " + id + " tipo " + tipoDeDonante);
        }

        List<MisionParticular> misionesCompletadas = donante.obtenerMisionesCompletadas();

        // Evento 4: Notificar al donante que cumplió una misión
        if (misionesCompletadas != null && !misionesCompletadas.isEmpty()) {
            for (MisionParticular mision : misionesCompletadas) {
                String nombreMision = mision.getMision().getNombre().toString();

                notificar(
                        donante.getIdEnDonaciones(),
                        "Felicitaciones!+Completaste+la+mision+" + nombreMision
                );

            }
        }

        // Evento 5: Notificar al donante que cambió de categoría
        if (this.pasaSiguienteCategoria(donante)) {
                String nuevaCategoria = donante.getMisionEnCurso().getMision().getCategoria().getCategoriaSiguiente().toString();
                notificar(
                        donante.getIdEnDonaciones(),
                        "Subiste+de+categoria!+Ahora+sos+donante+" + nuevaCategoria
                );

        }

        List <MisionParticularDTO> misionesCompletadasDTO = this.obtenerMisionParticularDTO(misionesCompletadas);

        return misionesCompletadasDTO;
    }

    public Boolean pasaSiguienteCategoria(Donante donante) {

        CategoriaDeMision categoriaActual = donante.getMisionEnCurso().getMision().getCategoria();

        List<MisionParticular> misionesParticularesDeDonante = this.misionParticularRepository.findByDonanteId(donante.getId());

        List<MisionParticular> misionesDeCategoriaActual = misionesParticularesDeDonante.stream()
                .filter(m -> m.getMision().getCategoria().getNombre().toString().equals(categoriaActual.getNombre().toString()))
                .toList();

        List<MisionParticular> misionesCompletadasDeCategoriaActual = misionesDeCategoriaActual.stream()
                .filter(m -> m.getEstado().equals(EstadoDeMision.COMPLETADA))
                .toList();

        return misionesCompletadasDeCategoriaActual.size() == categoriaActual.getMisiones().size();
    }

    private boolean traerNuevasDonaciones(Donante donante, URI uri) {
        ResponseEntity<DonacionIncentivosDTO[]> response = restTemplate.getForEntity(uri, DonacionIncentivosDTO[].class);
        DonacionIncentivosDTO[] arrayDeDonaciones = response.getBody();

        if(arrayDeDonaciones == null) return true;

        List<DonacionIncentivosDTO> donacionesActualizdasDTO = new ArrayList<>(Arrays.asList(arrayDeDonaciones));

        donante.setDonaciones(convertirDonacionesDTO(donacionesActualizdasDTO));
        return false;
    }

    @Override
    public List<InsigniaDTO> buscarInsigniasPorId(Long id,TipoDeDonante tipoDeDonante) {
        Donante donante = this.incentivosRepository.findByIdEnDonaciones(id, tipoDeDonante);
        if (donante == null) {
            throw new RuntimeException("No se encontro el donante con id: " + id + " tipo " + tipoDeDonante);
        }
        List<InsigniaDTO> insigniasDTO = this.obtenerInsigniasDTO(donante);

        return insigniasDTO;
    }

    @Override
    public MisionParticularDTO buscarMisionActualPorId(Long id, TipoDeDonante tipoDeDonante) {
        Donante donante = this.incentivosRepository.findByIdEnDonaciones(id, tipoDeDonante);
        if (donante == null) {
            throw new RuntimeException("No se encontro el donante con id: " + id + "tipo " + tipoDeDonante);
        }

        MisionParticularDTO misionActualDTO = new MisionParticularDTO(donante.getMisionEnCurso().getMision().getNombre().toString(), donante.getMisionEnCurso().getEstado());
        return misionActualDTO;
    }

    //TODO: Hacer que tome el query param de tipoDeDonante para que tambien pubique y difunda  insignias para los donantes juridicos

    @Override
    public String publicarYDifundirInsignia(Long id, Insignia insignia,
                                            String tipoDeDonante) {

        TipoDeDonante tipo = TipoDeDonante.valueOf(tipoDeDonante);
        Donante donante = incentivosRepository.findByIdEnDonaciones(id, tipo);

        if (donante == null) {
            System.out.println("Donante " + id + " no encontrado. Sincronizando...");

            try {
                String path = tipoDeDonante.equals("HUMANO")
                        ? "/humano/obtenerDonaciones/{id}"
                        : "/juridico/obtenerDonaciones/{id}";

                URI uri = UriComponentsBuilder
                        .fromUriString(propiedades.getUrl())
                        .path(path)
                        .buildAndExpand(id)
                        .toUri();

                ResponseEntity<PersonaDonanteDTO> response =
                        restTemplate.getForEntity(uri, PersonaDonanteDTO.class);
                PersonaDonanteDTO dto = response.getBody();

                if (dto != null) {
                    Donante nuevo = new Donante();
                    nuevo.setIdEnDonaciones(id);
                    nuevo.setTipoDeDonante(tipo);
                    nuevo.setNombre(dto.nombre());
                    nuevo.setApellido(dto.apellido());
                    nuevo.setRazonSocial(dto.razonSocial());

                    incentivosRepository.save(nuevo);
                    donante = nuevo;

                } else {
                    throw new RuntimeException("Donante no existe: " + id);
                }

            } catch (Exception e) {
                throw new RuntimeException("No se pudo recuperar el donante: " + id, e);
            }
        }

        String nombre = donante.getNombre() != null
                ? donante.getNombre() + " " +
                  (donante.getApellido() != null ? donante.getApellido() : "")
                : donante.getRazonSocial();

        return publicadorService.publicarYDifundirInsignia(nombre, insignia);
    }

    @Override
    public void calcularYGuardarRanking() {
        URI uri = UriComponentsBuilder
                .fromUriString(propiedades.getUrl())
                .path("/donantes")
                .build()
                .toUri();

        ResponseEntity<PersonaDonanteDTO[]> response =
                restTemplate.getForEntity(uri, PersonaDonanteDTO[].class);
        PersonaDonanteDTO[] array = response.getBody();
        if (array == null || array.length == 0) return;

        YearMonth mesPasado = YearMonth.now().minusMonths(1);

        List <Donante> donantes = Arrays.stream(array)
                .map(dto -> {
                    if(dto.idHumano() != null){
                        return incentivosRepository
                                .findByIdEnDonaciones(
                                        dto.idHumano(), TipoDeDonante.HUMANO
                                );
                    } else if (dto.idJuridico() != null){
                        return incentivosRepository
                                .findByIdEnDonaciones(
                                        dto.idJuridico(), TipoDeDonante.JURIDICO
                                );
                    }
                    return null;
                })
                .filter(d -> d != null)
                .toList();

        this.rankingCompletoOrdenado = donantes.stream()
                .sorted(Comparator.comparingInt(
                        (Donante d) -> -d.calcularMisionesCumplidasEn(mesPasado)))
                .toList();
        if(rankingCompletoOrdenado.size() < 3) return;

        RankingMensual nuevoRanking = new RankingMensual(
                LocalDate.now().minusMonths(1),
                rankingCompletoOrdenado.get(0),
                rankingCompletoOrdenado.get(1),
                rankingCompletoOrdenado.get(2),
                rankingCompletoOrdenado
        );

        rankingMensualRepository.save(nuevoRanking);
        this.ultimoRanking = nuevoRanking;
    }

    @Override
    public RankingMensual obtenerUltimoRanking() {
        return ultimoRanking;
    }


    public List<DonacionSegmentada> convertirDonacionesDTO(List<DonacionIncentivosDTO> donacionesDTO) {

        List<DonacionSegmentada> donaciones = donacionesDTO.stream().map(
                donacion -> {

                    return new DonacionSegmentada(
                            donacion.fechaDeIngreso(),
                            donacion.donacionEntregada(),
                            donacion.entidadBeneficiariaId(),
                            donacion.cantidadDeBienes(),
                            new Categoria(donacion.categoria())
                    );
                }).toList();

        return donaciones;
    }


    public List<MisionParticularDTO> obtenerMisionParticularDTO(List<MisionParticular> misiones) {

        List<MisionParticularDTO> misionesDTO = new ArrayList<>();
        misiones.forEach(m -> misionesDTO.add(new MisionParticularDTO(m.getMision().getNombre().toString(),m.getEstado())));
        return misionesDTO;
    }

    public List<InsigniaDTO> obtenerInsigniasDTO(Donante donante) {
        List<InsigniaDTO> insigniasDTO = donante.getPerfil().getInsignias().stream().map(i -> new InsigniaDTO(i.name(), i.texto())).toList();
        return insigniasDTO;
    }



    //TODO: Hacer que tome el query param de tipoDeDonante para que tambien obtenga las metricas de juridico
    @Override
    public MetricasImpactoDTO obtenerMetricasDeImpacto(Long idDonante) {

        Donante donante = incentivosRepository.findById(idDonante).orElseThrow(
                () -> new RuntimeException("Donante no encontrado: " + idDonante)
        );

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
    public String procesarLogro(Long id, Insignia insignia, TipoDeDonante tipoDeDonante) {
        Donante donante = incentivosRepository.findByIdEnDonaciones(id,tipoDeDonante);

        String nombre = donante.getNombre() != null
                ? donante.getNombre() + " " + donante.getApellido()
                : donante.getRazonSocial();
        return publicadorService.publicarYDifundirInsignia(nombre, insignia);
    }

    @Override
    public MetricasSistemaDTO obtenerMetricasDelSistema() {
        List<Donante> todos = incentivosRepository.findAll();
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
        List<RankingMensual> todosLosRankings = rankingMensualRepository.findAll();

        return todosLosRankings.stream()
                .filter(r -> (anio == null || r.getFecha().getYear() == anio))
                .filter(r -> (mes == null || r.getFecha().getMonthValue() == mes))
                .map(this::mapearDto)
                .toList();
    }

    // Método para llamar al servicio de notificaciones
    // URL resultante: http://localhost:8081/servicioDeNotificaciones/notificar?destinatario=X&mensaje=Y&medio=Z
    private void notificar(Long destinatarioId, String mensaje) {
        try {
            String url = notificacionesProperties.getUrl() +
                    "/servicioDeNotificaciones/notificar" +
                    "?destinatario=" + destinatarioId +
                    "&mensaje=" + mensaje;
            restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            System.out.println("Error al notificar: " + e.getMessage());
        }
    }

    public CategoriaDeMisionDTO crearCategoriaMision(CategoriaDeMisionDTO categoriaDto){
        if(categoriaDto == null){
            throw new RuntimeException("El DTO de categoria no puede ser nulo");
        }

        if(categoriaDto.nombre() == null){
            throw new RuntimeException("El nombre de la categoria no puede ser nulo");
        }
        if (!TipoCategoria.perteneceAEnum(categoriaDto.nombre().toString())) {
            throw new RuntimeException("El nombre de la categoria no pertenece a los nombres de categorias permitidas");
        }
        if(categoriaDto.categoriaSiguiente() == null && TipoCategoria.obtenerCategoriaSiguiente(categoriaDto.nombre()) != null){
            throw new RuntimeException("La categoria siguiente no puede ser nula");
        }
        CategoriaDeMision categoriaDeMision = new CategoriaDeMision(categoriaDto.nombre(), categoriaDto.categoriaSiguiente());

        this.categoriaDeMisionRepository.save(categoriaDeMision);

        return this.mapearAcategoriaDeMisionDTO(categoriaDeMision);

    }

    public CategoriaDeMisionDTO mapearAcategoriaDeMisionDTO(CategoriaDeMision categoria){
        if(categoria == null){
            throw new RuntimeException("La categoria no puede ser nula");
        }
        return new CategoriaDeMisionDTO(categoria.getId(), categoria.getNombre(), categoria.getCategoriaSiguiente());
    }

    @Transactional
     public MisionDTO crearMision(MisionDTO mision){
        if(mision == null){
            throw new RuntimeException("El DTO de mision no puede ser nulo");
        }

        if(mision.nombre() == null){
            throw new RuntimeException("El nombre de la mision no puede ser nulo");
        }
         if (!NombreDeMision.perteneceAEnum(mision.nombre().toString())) {
             throw new RuntimeException("El nombre de la mision no pertenece a los nombres de misiones permitidas");
         }
        if(mision.categoriaId() == null){
            throw new RuntimeException("La categoria de la mision no puede ser nula");
        }

        if(mision.ordenSecuencial() == 0 || mision.ordenSecuencial() < 0){
            throw new RuntimeException("El orden secuencial de la mision no puede ser cero o menor a 1");

        }
        CategoriaDeMision categoria = this.categoriaDeMisionRepository.findById(mision.categoriaId())
                .orElseThrow(() -> new RuntimeException("No se encontro la categoria con id: " + mision.categoriaId()));

        boolean ordenRepetido = categoria.getMisiones().stream()
                .anyMatch(m -> m.getOrdenSecuencial() == mision.ordenSecuencial());
        if (ordenRepetido) {
            throw new RuntimeException("Ya existe una misión con orden " + mision.ordenSecuencial()
                    + " en la categoría " + categoria.getNombre());
        }
        Mision nuevaMision = this.crearMisionSegunTipo(mision, categoria);
        categoria.agregarMision(nuevaMision);

        this.misionRepository.save(nuevaMision);

        return this.mapearAMisionDTO(nuevaMision);
     }

    public MisionDTO mapearAMisionDTO(Mision mision) {
        Long categoriaId = mision.getCategoria().getId();

        return switch (mision) {
            case Racha r -> new MisionDTO(
                    r.getId(), r.getNombre(), categoriaId, r.getOrdenSecuencial(), r.getInsigniaGanadora(),
                    r.getMeses(), r.getMesInicial(), r.getMesFinal(), null, null, null);

            case Completitud c -> new MisionDTO(
                    c.getId(), c.getNombre(), categoriaId, c.getOrdenSecuencial(), c.getInsigniaGanadora(),
                    null, null, null, c.getObjetivoDeCategoriasDistintas(), null, null);

            case DonacionesExitosas d -> new MisionDTO(
                    d.getId(), d.getNombre(), categoriaId, d.getOrdenSecuencial(), d.getInsigniaGanadora(),
                    null, null, null, null, d.getCantidadDeDonacioneExitosas(), null);

            case HabilDonador h -> new MisionDTO(
                    h.getId(), h.getNombre(), categoriaId, h.getOrdenSecuencial(), h.getInsigniaGanadora(),
                    null, null, null, null, null, h.getCantidadDeBienes());

            default -> throw new IllegalArgumentException("Tipo de misión no soportado: " + mision.getClass());
        };
    }

    public Mision crearMisionSegunTipo(MisionDTO dto, CategoriaDeMision categoria) {
        Mision mision = switch (dto.nombre()) {
            case RACHA               -> new Racha(dto.nombre(), dto.mesInicial(), dto.mesFinal());
            case COMPLETITUD         -> new Completitud(dto.nombre(), dto.objetivoDeCategoriasDistintas());
            case DONACIONES_EXITOSAS -> new DonacionesExitosas(dto.nombre(), dto.cantidadDeDonaciones());
            case HABIL_DONADOR       -> new HabilDonador(dto.nombre(), dto.cantidadDeBienes());
        };

        mision.setCategoria(categoria);
        mision.setOrdenSecuencial(dto.ordenSecuencial());
        mision.setInsigniaGanadora(dto.insigniaGanadora());
        return mision;
    }

    @Transactional(readOnly = true)
    public List<MisionFrontDTO> obtenerTodasLasMisionesPorId(Long id) {
        Donante donante = incentivosRepository.findByIdEnDonaciones(id, TipoDeDonante.HUMANO);
        if (donante == null) {
            throw new RuntimeException("No se encontró el donante con id: " + id);
        }

        List<MisionFrontDTO> misionesParaFront = donante.getMisiones().stream()
                .map(mp -> mapearAMisionFrontDTO(mp.getId(), mp.getMision(), mp.getEstado(), mp))
                .collect(Collectors.toCollection(ArrayList::new));

        Set<Long> idsAsignados = donante.getMisiones().stream()
                .map(mp -> mp.getMision().getId())
                .collect(Collectors.toSet());

        misionRepository.findAll().stream()
                .filter(m -> !idsAsignados.contains(m.getId()))
                .map(m -> mapearAMisionFrontDTO(m.getId(), m, EstadoDeMision.BLOQUEADA, null))
                .forEach(misionesParaFront::add);

        return misionesParaFront;
    }

    private MisionFrontDTO mapearAMisionFrontDTO(Long id, Mision mision, EstadoDeMision estado, MisionParticular mp) {
        return new MisionFrontDTO(
                id,
                obtenerNombreDeMisionParticular(mision),
                mision.getCategoria().getNombre().toString(),
                mision.getOrdenSecuencial(),
                mision.getInsigniaGanadora(),
                mision instanceof Racha r ? r.getMeses() : null,
                mision instanceof Completitud c ? c.getObjetivoDeCategoriasDistintas() : null,
                mision instanceof DonacionesExitosas d ? d.getCantidadDeDonacioneExitosas() : null,
                mision instanceof HabilDonador h ? h.getCantidadDeBienes() : null,
                estado,
                mp != null ? mp.getProgreso(): null,
                mp != null ? mp.getDistanciaDelObjetivo() : null
                );
    }

    public String obtenerNombreDeMisionParticular(Mision m) {
        return switch (m) {
            case  Racha r -> r.getNombre() + " de " + r.getMeses() + " meses";
            case Completitud c -> c.getNombre() + " de " + c.getObjetivoDeCategoriasDistintas();
            case DonacionesExitosas d -> d.getNombre() + " de " + d.getCantidadDeDonacioneExitosas();
            case HabilDonador h -> h.getNombre() + " de " + h.getCantidadDeBienes();
            default -> "Sin nombre de misión";
        };
    }
}