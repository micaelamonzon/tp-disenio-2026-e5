package ar.edu.utn.frba.ddsi.donaciones.controllers;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
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

    @GetMapping("/{id}")
    public ResponseEntity<PropuestaMatch> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matchmakingService.obtenerPropuestaPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/seleccionar")
    public ResponseEntity<PropuestaMatch> seleccionar(
            @PathVariable Long id,
            @RequestParam Long necesidadId) {
        try {
            return ResponseEntity.ok(matchmakingService.seleccionarNecesidad(id, necesidadId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}