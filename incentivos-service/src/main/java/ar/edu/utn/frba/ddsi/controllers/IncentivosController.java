package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingPosicion;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.repositories.RankingMensualRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.impl.InsigniaPublicadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/incentivos-service")
public class IncentivosController {
    private final IncentivosService incentivosService;
    private final IncentivosRepository incentivosRepository;
    private final InsigniaPublicadorService insigniaPublicadorService;
    private final RankingMensualRepository rankingMensualRepository;

    public IncentivosController(IncentivosService incentivosService, IncentivosRepository incentivosRepository, InsigniaPublicadorService insigniaPublicadorService, RankingMensualRepository rankingMensualRepository) {
        this.incentivosService = incentivosService;
        this.incentivosRepository = incentivosRepository;
        this.insigniaPublicadorService = insigniaPublicadorService;
        this.rankingMensualRepository = rankingMensualRepository;
    }


    @PostMapping("donante/{id}/mision/{misionId}")
    @ResponseStatus(HttpStatus.CREATED) //para devolver el 201
    public void agregarMisionADonante(@PathVariable Long id, @RequestParam TipoDeDonante tipoDeDonante, @PathVariable Long misionId) {
        incentivosService.agregarMisionADonante(id, tipoDeDonante,misionId); //sirve tanto para humano como para juridico
    }

    //Obtención de las misiones completadas por una persona donante.
    @GetMapping("donante/misionesCompletadas/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED) //para devolver el 200
    public List<MisionParticularDTO>  obtenerTodasLasMisionesDeDonanteHumano(@PathVariable Long id, @RequestParam TipoDeDonante tipoDeDonante){
        return incentivosService.obtenerMisionesCompletadas(id, tipoDeDonante);
    }

    //Obtención de las insignias para una persona donante.
    @GetMapping("donante/insignias/{id}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<InsigniaDTO> obtenerInsignias(@PathVariable Long id, @RequestParam TipoDeDonante tipoDeDonante){
        try{
            return incentivosService.buscarInsigniasPorId(id, tipoDeDonante);
        }catch(RuntimeException e){
            throw new RuntimeException("Error al obtener las insignias porque " + e.getMessage());
        }

    }
    @GetMapping("donante/misionActual/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MisionParticularDTO obtenerMisionEnCurso(@PathVariable Long id, @RequestParam TipoDeDonante tipoDeDonante){
        try{
            return incentivosService.buscarMisionActualPorId(id, tipoDeDonante);
        }catch(RuntimeException e){
            throw new RuntimeException("Error al obtener la mision actual porque " + e.getMessage());
        }
    }

    @PostMapping("/donante/{id}/insignias/{insignia}/difundir")
    public ResponseEntity<String> difundirInsignia(@PathVariable Long id,
                                                   @PathVariable String insignia,
                                                   @RequestParam TipoDeDonante tipoDeDonante) {
        String resultado = incentivosService.publicarYDifundirInsignia(id, insignia, tipoDeDonante.name());
        return ResponseEntity.accepted().body(resultado);
    }

    @GetMapping("/admin/metricas-sistema")
    public ResponseEntity<MetricasSistemaDTO> obtenerMetricasSistema() {
        return ResponseEntity.ok(incentivosService.obtenerMetricasDelSistema());
    }

    @GetMapping("/{id}/metricas")
    public ResponseEntity<MetricasImpactoDTO> obtenerMetricas(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                incentivosService.obtenerMetricasDeImpacto(id));
    }

    @GetMapping("/ranking/ultimo")
    public ResponseEntity<RankingMensualDTO> obtenerUltimoRanking() {
        Optional<RankingMensual> ultimoRankingOpt = rankingMensualRepository.findTopByOrderByFechaDesc();

        if (ultimoRankingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RankingMensual ranking = ultimoRankingOpt.get();
        RankingMensualDTO dto = convertirARankingMensualDTO(ranking);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/ranking/posicion/{id}")
    public ResponseEntity<Integer> obtenerPosicionDonante(@PathVariable Long id,
                                                          @RequestParam TipoDeDonante tipoDeDonante) {
        Integer posicion = incentivosService.obtenerPosicion(id, tipoDeDonante);
        return posicion == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(posicion);
    }

    @GetMapping("/ranking/historial")
    public ResponseEntity<List<RankingMensualDTO>> buscarRankings(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {

        List<RankingMensual> rankings = rankingMensualRepository.findAll();

        List<RankingMensualDTO> dtos = rankings.stream()
                .filter(r -> {
                    boolean coincideMes = (mes == null || r.getFecha().getMonthValue() == mes);
                    boolean coincideAnio = (anio == null || r.getFecha().getYear() == anio);
                    return coincideMes && coincideAnio;
                })
                .map(this::convertirARankingMensualDTO)
                .collect(Collectors.toList());

        if (dtos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/ranking/calcular")
    public ResponseEntity<Void> forzarCalculoRanking() {
        incentivosService.calcularYGuardarRanking();
        return ResponseEntity.ok().build();
    }
    private RankingMensualDTO convertirARankingMensualDTO(RankingMensual ranking) {
        return new RankingMensualDTO(
                ranking.getFecha(),
                ranking.getNombrePrimerPuesto(),
                ranking.getNombreSegundoPuesto(),
                ranking.getNombreTercerPuesto(),
                ranking.getMisionesPrimerPuesto() != null ? ranking.getMisionesPrimerPuesto() : 0,
                ranking.getMisionesSegundoPuesto() != null ? ranking.getMisionesSegundoPuesto(): 0,
                ranking.getMisionesTercerPuesto() != null ? ranking.getMisionesTercerPuesto() : 0
        );
    }

    @PostMapping("/categoriaMision")
    public ResponseEntity<CategoriaDeMisionDTO> crearNuevaCategoriaDeMision(@RequestBody CategoriaDeMisionDTO categoria) {
        CategoriaDeMisionDTO nuevaCategoria = incentivosService.crearCategoriaMision(categoria);
        if (nuevaCategoria == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(nuevaCategoria);
    }

    @PostMapping("/mision")
    public ResponseEntity<MisionDTO> crearNuevaMision(@RequestBody MisionDTO mision){
        MisionDTO nuevaMision = incentivosService.crearMision(mision);
        if (nuevaMision == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(nuevaMision);
    }

    @GetMapping("/misiones/todas/{id}")
    public ResponseEntity<List<MisionFrontDTO>> obtenerTodasLasMisionesPorId(@PathVariable Long id,  @RequestParam TipoDeDonante tipoDeDonante) {
        List<MisionFrontDTO> misiones = incentivosService.obtenerTodasLasMisionesPorId(id, tipoDeDonante);
        if (misiones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(misiones);
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<PerfilDTO> obtenerPerfilPorId(@PathVariable Long id, @RequestParam TipoDeDonante tipoDeDonante) {
        PerfilDTO perfil = incentivosService.obtenerPerfilPorId(id,tipoDeDonante);
        if (perfil == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(perfil);
    }
}
