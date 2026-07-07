package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.dto.CamionRequestDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.services.Impl.CamionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistica-service/")
public class CamionController {

    private final CamionServiceImpl camionService;

    public CamionController(CamionServiceImpl camionService) {
        this.camionService = camionService;
    }

    @PostMapping("/camiones")
    public ResponseEntity<CamionResponseDTO> crearCamion(@RequestBody CamionRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(camionService.crearCamion(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/camiones")
    public ResponseEntity<List<CamionResponseDTO>> listarCamiones(
            @RequestParam(required = false) Boolean disponible) {
        return ResponseEntity.ok(camionService.listarCamiones(disponible));
    }

    @GetMapping("/camiones/{id}")
    public ResponseEntity<CamionResponseDTO> obtenerCamion(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(camionService.obtenerCamionPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/camiones/{id}")
    public ResponseEntity<CamionResponseDTO> actualizarCamion(
            @PathVariable Long id,
            @RequestBody CamionRequestDTO request) {
        try {
            return ResponseEntity.ok(camionService.actualizarCamion(id, request));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/camiones/{id}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable Long id) {
        try {
            camionService.eliminarCamion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}