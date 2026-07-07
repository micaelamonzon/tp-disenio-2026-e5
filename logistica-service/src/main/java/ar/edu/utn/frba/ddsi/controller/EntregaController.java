package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.dto.ConfirmarRecepcionDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.services.Impl.EntregaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistica-service/")

public class EntregaController {
    private final EntregaServiceImpl entregaService;

    public EntregaController(EntregaServiceImpl entregaService) {
        this.entregaService = entregaService;
    }


    @PostMapping("/entrega")
    // Adaptamos al nuevo modelo: la entrega recibe una lista de donaciones,
    // el donante asociado, y ya no se le indica el camión al crearla
    public ResponseEntity<EntregaDTO> crearEntrega(
            @RequestParam List<Long> donacionesIds,
            @RequestParam Long entidadId,
            @RequestParam Long donanteId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(entregaService.crearEntrega(donacionesIds, entidadId, donanteId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/entrega/{id}/iniciarTraslado")
    public ResponseEntity<EntregaDTO> iniciarTraslado(
            @PathVariable Long id,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(entregaService.iniciarTraslado(id,responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/entrega/{id}/confirmarRecepcion")
    public ResponseEntity<EntregaDTO> confirmarRecepcion(
            @PathVariable Long id,
            @RequestParam String responsableId,
            @RequestBody ConfirmarRecepcionDTO body) {
        try {
            return ResponseEntity.ok(entregaService.confirmarRecepcion(id, body, responsableId));
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/entrega/{id}/marcarNoRecibida")
    public ResponseEntity<EntregaDTO> noRecibida(
            @PathVariable Long id,
            @RequestParam String responsableId,
            @RequestBody ConfirmarRecepcionDTO body) {
        try {
            return ResponseEntity.ok(entregaService.marcarNoRecibida(id, body, responsableId));
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/entrega/{id}/volverAPendiente")
    public ResponseEntity<EntregaDTO> volverAPendiente(
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(entregaService.volverAPendiente(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // El chofer informa el comienzo de su ruta: todas las entregas pasan a En traslado
    @PatchMapping("/ruta/iniciar")
    public ResponseEntity<List<EntregaDTO>> iniciarRuta(
            @RequestParam List<Long> entregaIds,
            @RequestParam String choferId) {
        try {
            return ResponseEntity.ok(entregaService.iniciarRuta(entregaIds, choferId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}