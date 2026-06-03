package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoRequestDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.NecesidadDTO;
import ar.edu.utn.frba.ddsi.donaciones.services.NecesidadService;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")
public class NecesidadController {

    private final NecesidadService necesidadService;

    public NecesidadController(NecesidadService necesidadService) {
        this.necesidadService = necesidadService;
    }

    @GetMapping("/entidad/{idEntidad}/necesidades")
    public ResponseEntity<List<NecesidadDTO>> obtenerNecesidades(@PathVariable Long idEntidad) {
        return ResponseEntity.ok(necesidadService.obtenerNecesidades(idEntidad));
    }

    @PostMapping("/entidad/{idEntidad}/necesidad")
    public ResponseEntity<NecesidadDTO> crearNecesidad(
            @PathVariable Long idEntidad,
            @RequestBody NecesidadDTO body) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(necesidadService.crearNecesidad(idEntidad, body));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/entidad/{idEntidad}/necesidad/{idNecesidad}")
    public ResponseEntity<NecesidadDTO> modificarNecesidad(
            @PathVariable Long idEntidad,
            @PathVariable Long idNecesidad,
            @RequestBody NecesidadDTO body) {
        try {
            return ResponseEntity.ok(necesidadService.modificarNecesidad(idEntidad, idNecesidad, body));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/entidad/{idEntidad}/necesidad/{idNecesidad}")
    public ResponseEntity<Void> eliminarNecesidad(
            @PathVariable Long idEntidad,
            @PathVariable Long idNecesidad) {
        try {
            necesidadService.eliminarNecesidad(idEntidad, idNecesidad);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
