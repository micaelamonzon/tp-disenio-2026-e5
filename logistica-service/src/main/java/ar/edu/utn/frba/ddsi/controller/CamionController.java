package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.dto.CamionDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryRutas;
import ar.edu.utn.frba.ddsi.services.Impl.CamionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistica-service/")
public class CamionController {

    private final CamionServiceImpl camionService;
    private final RepositoryRutas repositoryRutas;

    public CamionController(CamionServiceImpl camionService, RepositoryRutas repositoryRutas) {
        this.camionService = camionService;
        this.repositoryRutas = repositoryRutas;
    }

    @PostMapping("/camiones")
    public ResponseEntity<CamionResponseDTO> crearCamion(@RequestBody CamionDTO request) {
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
    public ResponseEntity<CamionResponseDTO> obtenerCamion(@PathVariable String id) {
        try {
            return ResponseEntity.ok(camionService.obtenerCamionPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/camiones/{id}")
    public ResponseEntity<CamionResponseDTO> actualizarCamion(
            @PathVariable String id,
            @RequestBody CamionDTO request) {
        try {
            return ResponseEntity.ok(camionService.actualizarCamion(id, request));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/camiones/{id}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable String id) {
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