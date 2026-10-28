package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.services.Impl.PlanificacionRutas;
import ar.edu.utn.frba.ddsi.repository.impl.SolicitudPlanificacionRepository;
import ar.edu.utn.frba.ddsi.services.Impl.SolicitudPlanificacionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logistica")
public class PlanificacionRutasController {
    private final PlanificacionRutas planificacionRutas;
    private final SolicitudPlanificacionRepository solicitudRepository;

    public PlanificacionRutasController(PlanificacionRutas planificacionRutas, SolicitudPlanificacionRepository solicitudRepository) {
        this.planificacionRutas = planificacionRutas;
        this.solicitudRepository = solicitudRepository;
    }

    @GetMapping("/route-plans/{requestId}")
    public ResponseEntity<?> obtenerPlanificacion(@PathVariable String requestId) {
        return solicitudRepository.findById(requestId)
                .<ResponseEntity<?>>map(s -> switch (s.getEstado()) {
                    case PENDIENTE -> ResponseEntity.accepted().body(s.getEstado());
                    case COMPLETADA -> ResponseEntity.ok(s.getResultadoJson());
                    case FALLIDA -> ResponseEntity.unprocessableEntity().body(s.getMensajeError());
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
