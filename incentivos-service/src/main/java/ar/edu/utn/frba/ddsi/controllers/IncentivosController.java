package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.impl.InsigniaPublicadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/incentivos-service")
public class IncentivosController {
    private final IncentivosService incentivosService;
    private final IncentivosRepository incentivosRepository;
    private final InsigniaPublicadorService insigniaPublicadorService;

    public IncentivosController(IncentivosService incentivosService, IncentivosRepository incentivosRepository, InsigniaPublicadorService insigniaPublicadorService) {
        this.incentivosService = incentivosService;
        this.incentivosRepository = incentivosRepository;
        this.insigniaPublicadorService = insigniaPublicadorService;
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

    @GetMapping("/verificarMision/{idPersona}")
    public ResponseEntity<String> verificarMision(
            @PathVariable("idPersona") Long id,
            @RequestParam(name = "tipoDonante", defaultValue = "HUMANO")
            String tipoDonante) {

        Insignia insigniaObtenida = Insignia.DONACIONEXITOSA;

        String resultado = incentivosService.publicarYDifundirInsignia(
                id, insigniaObtenida, tipoDonante);

        return ResponseEntity.ok(resultado);
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



    @GetMapping("/ranking") // top 3
    public ResponseEntity<RankingMensualDTO> obtenerRankingActual() {
        RankingMensualDTO dto = incentivosService.obtenerUltimoRankingDTO();
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/ranking/posicion/{id}") // busco donante por ID
    public ResponseEntity<Integer> obtenerPosicionDonante(@PathVariable Long id) {
        RankingMensual ranking = incentivosService.obtenerUltimoRanking();
        if (ranking == null) return ResponseEntity.noContent().build();
        Integer posicion = ranking.getPosicionPorId(id);
        if (posicion == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(posicion);
    }
    @GetMapping("/ranking/historial")
    public ResponseEntity<List<RankingMensualDTO>> obtenerHistorial(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        List<RankingMensualDTO> historial = incentivosService.buscarRankings(mes, anio);
        if (historial.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(historial);
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
    public ResponseEntity<List<MisionFrontDTO>> obtenerTodasLasMisionesPorId(@PathVariable Long id) {
        List<MisionFrontDTO> misiones = incentivosService.obtenerTodasLasMisionesPorId(id);
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
