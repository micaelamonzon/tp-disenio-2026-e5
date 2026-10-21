package ar.edu.utn.frba.ddsi.donaciones.controllers;
import ar.edu.utn.frba.ddsi.donaciones.dto.PropuestaMatchDTO;
import ar.edu.utn.frba.ddsi.donaciones.services.MatchmakingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicioDeDonaciones/matchmaking")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @PostMapping("/ejecutar")
    public ResponseEntity<Void> ejecutar() {
        matchmakingService.ejecutarProcesoMatchmaking();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/donacion/{donacionId}")
    public PropuestaMatchDTO obtenerPorDonacion(@PathVariable Long donacionId) {
        return matchmakingService.obtenerPropuestaDTODeDonacion(donacionId);
    }

    @PostMapping("/{propuestaId}/seleccionar")
    public ResponseEntity<Void> seleccionar(@PathVariable Long propuestaId,
                                            @RequestParam Long necesidadId,
                                            @RequestParam String responsableId) {
        matchmakingService.seleccionarNecesidad(propuestaId, necesidadId, responsableId);
        return ResponseEntity.noContent().build();
    }
    }
}