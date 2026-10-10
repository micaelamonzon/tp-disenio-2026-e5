package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.services.Impl.LogisticaScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logistica-service/admin")
public class PlanificacionManualController {
    private final LogisticaScheduler scheduler;

    public PlanificacionManualController(LogisticaScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/planificar")
    public ResponseEntity<Void> planificar() {
        scheduler.planificadorRutasDelDiaSiguiente();
        return ResponseEntity.accepted().build();
    }
}
