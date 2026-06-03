package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incentivos-service")
public class IncentivosController {
    private final IncentivosService incentivosService;


    public IncentivosController(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
    }

    //Obtención de las misiones completadas por una persona donante.
    @GetMapping("donanteHumano/misionesCompletadas/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED) //para devolver el 200
    public List<MisionDTO> obtenerTodasLasMisionesDeDonanteHumano(@PathVariable Long id){
        System.out.println("Antes del GET");
        return incentivosService.obtenerDonanteHumano(id);
    }

    @GetMapping("donanteJuridico/misionesCompletadas/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED) //para devolver el 200
    public List<MisionDTO> obtenerTodasLasMisionesDeDonanteJuridico(@PathVariable Long id){
        return incentivosService.obtenerDonanteJuridico(id);
    }
    //Obtención de las insignias para una persona donante.
    @GetMapping("{id}/insignias")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<InsigniaDTO> obtenerInsignias(@PathVariable Long id){
        try{
            return incentivosService.buscarInsigniasPorId(id);
        }catch(RuntimeException e){
            throw new RuntimeException("Error al obtener las insignias porque " + e.getMessage());
        }

    }
    @GetMapping("{id}/misionActual")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MisionDTO obtenerMisionEnCurso(@PathVariable Long id){
        try{
            return incentivosService.buscarMisionActualPorId(id);
        }catch(RuntimeException e){
            throw new RuntimeException("Error al obtener la mision actual porque " + e.getMessage());
        }
    }

    @GetMapping("/verificarMision/{idPersona}")
    //    Insignia insigniaObtenida = Insignia.COLABORADOR; // Antes del merge
    public ResponseEntity<Void> verificarMision(@PathVariable Long id){
        Insignia insigniaObtenida = Insignia.DONACIONEXITOSA; // ejemplo

        incentivosService.publicarYDifundirInsignia(id, insigniaObtenida);

        return ResponseEntity.ok().build();
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
    @GetMapping("/ranking/posicion/{idDonante}") // busco donante por ID
    public ResponseEntity<Integer> obtenerPosicionDonante(@PathVariable Long idDonante) {
        RankingMensual ranking = incentivosService.obtenerUltimoRanking();
        if (ranking == null) return ResponseEntity.noContent().build();
        Integer posicion = ranking.getPosicionPorId(idDonante);
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
}
