package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.dto.PlanningResultDTO;
import ar.edu.utn.frba.ddsi.services.Impl.PlanificacionRutas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logistica")
public class PlanificacionRutasController {
    private final PlanificacionRutas planificacionRutas;

    public PlanificacionRutasController(PlanificacionRutas planificacionRutas) {
        this.planificacionRutas = planificacionRutas;
    }

    //Endpoint para consultar la ruta
    @GetMapping("/route-plans/{id}")
    public ResponseEntity<PlanningResultDTO> obtenerPlanificacion(@PathVariable String id) {
        try {
            PlanningResultDTO resultado = planificacionRutas.obtenerPlanificacionPorId(id);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
