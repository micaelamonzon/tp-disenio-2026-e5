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
import ar.edu.utn.frba.ddsi.models.entities.misiones.Completitud;
import ar.edu.utn.frba.ddsi.models.entities.misiones.DonacionesExitosas;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Estado;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.HabilDonador;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import ar.edu.utn.frba.ddsi.models.entities.misiones.NombreDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Racha;
import ar.edu.utn.frba.ddsi.models.entities.persona.*;
import ar.edu.utn.frba.ddsi.repositories.CategoriaDeMisionRepository;
import ar.edu.utn.frba.ddsi.repositories.EstadoMisionRepository;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.repositories.MisionParticularRepository;
import ar.edu.utn.frba.ddsi.repositories.MisionRepository;
import ar.edu.utn.frba.ddsi.repositories.RankingMensualRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.YearMonth;
import java.util.*;
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
    private final EstadoMisionRepository estadoMisionRepository;

    public IncentivosServiceImpl(IncentivosRepository incentivosRepository, RestTemplate restTemplate, RestProperties propiedades,
                                 InsigniaPublicadorService publicadorService, NotificacionesProperties notificacionesProperties,
                                 RankingMensualRepository rankingMensualRepository, MisionRepository misionRepository,
                                 CategoriaDeMisionRepository categoriaDeMisionRepository, MisionParticularRepository misionParticularRepository,
                                 EstadoMisionRepository estadoMisionRepository) {
        this.incentivosRepository = incentivosRepository;
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.publicadorService = publicadorService;
        this.notificacionesProperties = notificacionesProperties;
        this.rankingMensualRepository = rankingMensualRepository;
        this.misionRepository = misionRepository;
        this.categoriaDeMisionRepository = categoriaDeMisionRepository;
        this.misionParticularRepository = misionParticularRepository;
        this.estadoMisionRepository = estadoMisionRepository;

    }



    @Override
    @Transactional
    public void agregarMisionADonante(Long id, TipoDeDonante tipoDeDonante, Long misionId){

       Mision mision  = this.misionRepository.findById(misionId).orElseThrow(() -> new RuntimeException("Misión no encontrada"));

       Donante donante = incentivosRepository.findByIdEnDonaciones(id,tipoDeDonante);

       MisionParticular misionParticular = new MisionParticular(mision, donante);

       misionParticularRepository.save(misionParticular);

       Estado nuevoEstado = new Estado(EstadoDeMision.ACTUAL, donante, misionParticular);

       misionParticular.agregarEstado(nuevoEstado); //aca dentro se le setea en false al estadoAnterior

       this.estadoMisionRepository.save(nuevoEstado);

        donante.agregarMision(misionParticular);
        donante.setMisionEnCurso(misionParticular);

        if(donante.getPerfil() != null && misionParticular.getMision().getCategoria() != null) {
            donante.getPerfil().setCategoria(misionParticular.getMision().getCategoria());
        }
    }

    @Transactional
    @Override
    public List<MisionParticularDTO> obtenerMisionesCompletadas(Long id,TipoDeDonante tipoDeDonante) {

        Donante donante = this.incentivosRepository.findByIdEnDonaciones(id,tipoDeDonante);

        if(donante == null){
            throw new RuntimeException("No se encontro el donante con id: " + id + " tipo " + tipoDeDonante);
        }

        List<MisionParticular> misionesCompletadas = donante.obtenerMisionesCompletadas();

        System.out.println(misionesCompletadas);

        // Evento 4: Notificar al donante que cumplió una misión
        if (misionesCompletadas != null && !misionesCompletadas.isEmpty()) {
            for (MisionParticular mision : misionesCompletadas) {
                String nombreMision = mision.getMision().getNombre().toString();
                String insigniaGanadora = mision.getMision().getInsigniaGanadora();

                try {
                    this.publicarYDifundirInsignia(donante.getIdEnDonaciones(), insigniaGanadora, tipoDeDonante.name());
                } catch (Exception e) {
                    System.out.println("Error al difundir la insignia automáticamente en n8n: " + e.getMessage());
                }
                
                notificar(
                        donante.getIdEnDonaciones(),
                        "Felicitaciones!+Completaste+la+mision+" + nombreMision
                );

            }
        }

        // Evento 5: Notificar al donante que cambió de categoría
        if (this.pasaSiguienteCategoria(donante) && donante.getMisionEnCurso() != null) {

                String nuevaCategoria = donante.getMisionEnCurso().getMision().getCategoria().getCategoriaSiguiente().toString();
                notificar(
                        donante.getIdEnDonaciones(),
                        "Subiste+de+categoria!+Ahora+sos+donante+" + nuevaCategoria
                );

        }

        List <MisionParticularDTO> misionesCompletadasDTO = this.obtenerMisionParticularDTO(misionesCompletadas);

        return misionesCompletadasDTO;
    }

    @Override
    public Boolean pasaSiguienteCategoria(Donante donante) {

        MisionParticular enCurso = donante.getMisionEnCurso();
        if (enCurso == null) {
            return false;
        }

        CategoriaDeMision categoriaActual = donante.getMisionEnCurso().getMision().getCategoria();

        List<MisionParticular> misionesParticularesDeCategoriaActual = donante.getMisiones().stream()
                .filter(m -> m.getMision().getCategoria().getNombre().toString().equals(categoriaActual.getNombre().toString()))
                .toList();


        List<MisionParticular> misionesParticularesCompletadasDeCategoriaActual = misionesParticularesDeCategoriaActual.stream()
                .filter(m -> m.getEstado().equals(EstadoDeMision.COMPLETADA))
                .toList();

        return misionesParticularesCompletadasDeCategoriaActual.size() == categoriaActual.getMisiones().size();
    }


    @Transactional
    public void setearSiguienteCategoria(Donante donante) {
        CategoriaDeMision categoriaActual = donante.getMisionEnCurso().getMision().getCategoria();
        CategoriaDeMision categoriaSiguiente = this.categoriaDeMisionRepository.findByNombre(categoriaActual.getCategoriaSiguiente());
        if(categoriaSiguiente == null || categoriaSiguiente == categoriaActual){
            donante.getPerfil().setCategoria(categoriaActual);
            donante.setMisionEnCurso(null);
            return;
        }
        donante.getPerfil().setCategoria(categoriaSiguiente);
        this.agregarMisionADonante(donante.getIdEnDonaciones(),donante.getTipoDeDonante(), categoriaSiguiente.getMisiones().get(0).getId());

    }

    public void pasaASiguienteMisionDeLaMismaCategoria(Donante donante){

        CategoriaDeMision categoriaActual = donante.getMisionEnCurso().getMision().getCategoria();

        List<Mision> misionesDeCategoriaActual = categoriaActual.getMisiones();

        MisionParticular misionEnCurso = donante.getMisionEnCurso();

        if (misionEnCurso.getMision().getOrdenSecuencial() < misionesDeCategoriaActual.size()) {
            Mision siguienteMision = misionesDeCategoriaActual.get(misionEnCurso.getMision().getOrdenSecuencial() - 1);
            this.agregarMisionADonante(donante.getIdEnDonaciones(),donante.getTipoDeDonante(),siguienteMision.getId());
        } else {
            donante.setMisionEnCurso(null);
        }
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
    public String publicarYDifundirInsignia(Long id, String insignia,
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

    @Transactional
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

        //YearMonth mesPasado = YearMonth.now().minusMonths(1);
        YearMonth mesActual = YearMonth.now();

        List<Donante> donantes = Arrays.stream(array)
                .map(dto -> {
                    if (dto.idHumano() != null) {
                        return incentivosRepository.findByIdEnDonaciones(dto.idHumano(), TipoDeDonante.HUMANO);
                    } else if (dto.idJuridico() != null) {
                        return incentivosRepository.findByIdEnDonaciones(dto.idJuridico(), TipoDeDonante.JURIDICO);
                    }
                    return null;
                })
                .filter(d -> d != null)
                .toList();

        List<Donante> ordenados = donantes.stream()
                .filter(d -> d.calcularMisionesCumplidasEn(mesActual) > 0)
                .sorted(Comparator.comparingInt(
                        (Donante d) -> -d.calcularMisionesCumplidasEn(mesActual)))
                .toList();
        if (ordenados.isEmpty()) return;

        // recalcular reemplaza el ranking de ese mes en vez de duplicarlo
        rankingMensualRepository.findAll().stream()
                .filter(r -> YearMonth.from(r.getFecha()).equals(mesActual))
                .forEach(rankingMensualRepository::delete);

        RankingMensual nuevo = new RankingMensual(
                mesActual.atDay(1),
                ordenados.get(0),
                ordenados.size() > 1 ? ordenados.get(1) : null,
                ordenados.size() > 2 ? ordenados.get(2) : null,
                ordenados);

        rankingMensualRepository.save(nuevo);

    }
    @Override
    @Transactional(readOnly = true)
    public RankingMensual obtenerUltimoRanking() {
        return rankingMensualRepository.findTopByOrderByFechaDesc().orElse(null);
    }

    @Transactional(readOnly = true)
    public Integer obtenerPosicion(Long idEnDonaciones, TipoDeDonante tipo) {
        RankingMensual ranking = rankingMensualRepository.findTopByOrderByFechaDesc().orElse(null);
        Donante donante = incentivosRepository.findByIdEnDonaciones(idEnDonaciones, tipo);
        if (ranking == null || donante == null) return null;
        return ranking.getPosicion(donante);
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
        misiones.forEach(m -> misionesDTO.add(
                new MisionParticularDTO(
                        m.getMision() != null ? this.obtenerNombreDeMisionParticular(m.getMision()): null,
                        m.getEstado())));
        return misionesDTO;
    }

    public List<InsigniaDTO> obtenerInsigniasDTO(Donante donante) {

        List<MisionParticular> misionesCompletadas = donante.obtenerMisionesCompletadas();
        List<InsigniaDTO> insiginas = misionesCompletadas.stream()
                .map(m -> new InsigniaDTO(this.obtenerNombreDeMisionParticular(m.getMision()),m.getMision().getInsigniaGanadora())).toList();

        return insiginas;
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
    public String procesarLogro(Long id, String insignia, TipoDeDonante tipoDeDonante) {
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

    private RankingMensualDTO mapearDto(RankingMensual r) {
        return new RankingMensualDTO(
                r.getFecha(),
                nombreDe(r.getPrimerPuesto()),
                nombreDe(r.getSegundoPuesto()),
                nombreDe(r.getTercerPuesto()),
                r.getMisionesPrimerPuesto(),
                r.getMisionesSegundoPuesto(),
                r.getMisionesTercerPuesto() );
    }

    private String nombreDe(Donante d) {
        if (d == null) return null;
        return d.getNombre() != null ? d.getNombre() + " " + d.getApellido() : d.getRazonSocial();
    }

    @Transactional
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
    public List<MisionFrontDTO> obtenerTodasLasMisionesPorId(Long id, TipoDeDonante tipoDeDonante) {
        Donante donante = incentivosRepository.findByIdEnDonaciones(id,tipoDeDonante);
        if (donante == null) {
            throw new RuntimeException("No se encontró el donante con id : " + id + "TIPO" + tipoDeDonante);
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
        m = (Mision) Hibernate.unproxy(m);

        return switch (m) {
            case Racha r -> r.getNombre() + " de " + r.getMeses() + " meses";
            case Completitud c -> c.getNombre() + " de " + c.getObjetivoDeCategoriasDistintas();
            case DonacionesExitosas d -> this.pulirNombre(d.getNombre())+ " de " + d.getCantidadDeDonacioneExitosas();
            case HabilDonador h -> this.pulirNombre( h.getNombre()) + " de " + h.getCantidadDeBienes();
            default -> "Sin nombre de misión";
        };
    }

    public String pulirNombre(NombreDeMision nombreDeMision) {
        return switch (nombreDeMision){
            case DONACIONES_EXITOSAS -> "DONACIONES EXITOSAS";
            case HABIL_DONADOR -> "HÁBIL DONADOR";
            default ->  " ";
        };
    }

    @Transactional()
    public PerfilDTO obtenerPerfilPorId(Long idEnDonaciones, TipoDeDonante tipoDeDonante) {
        Donante donante = incentivosRepository.findByIdEnDonaciones(idEnDonaciones, tipoDeDonante);
        if (donante == null) {
            throw new RuntimeException("No se encontró el donante con id: " + idEnDonaciones + " y tipo: " + tipoDeDonante);
        }
        Perfil perfil = donante.getPerfil();
        Integer cantMisionesCumplidas = donante.obtenerMisionesCompletadas().size();
        Integer cantInsignias = perfil.getInsignias().size();
        List<InsigniaDTO> insigniasDTO = this.obtenerInsigniasDTO(donante);
        RankingMensual rankingActual = obtenerUltimoRanking();
        if (rankingActual == null) {
            calcularYGuardarRanking();
            rankingActual = obtenerUltimoRanking();
        }
        
        Integer puestoRanking = null;
        if (rankingActual != null) {
            puestoRanking = rankingActual.getPosicion(donante);
        }

        return new PerfilDTO(
                cantMisionesCumplidas,
                cantInsignias,
                perfil.getNombreDeUsuario(),
                insigniasDTO,
                perfil.getCategoria() != null ? perfil.getCategoria().getNombre().toString() : null,
                puestoRanking
        );
    }
}