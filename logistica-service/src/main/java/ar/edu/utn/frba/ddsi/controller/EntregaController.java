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
            @RequestParam List<Long> donantesIds) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(entregaService.crearEntregaDTO(donacionesIds, entidadId, donantesIds));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PatchMapping("/entrega/{idDonante}/iniciarTraslado")
    public ResponseEntity<List<EntregaDTO>> iniciarTraslado(
            @PathVariable Long idDonante,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(entregaService.iniciarTraslado(idDonante,responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/entrega/{id}/confirmarRecepcion")
    public ResponseEntity<EntregaDTO> confirmarRecepcion(
            @PathVariable String id,
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
            @PathVariable String id,
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
            @PathVariable String id) {
        try {
            return ResponseEntity.ok(entregaService.volverAPendiente(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    // El chofer informa el comienzo de su ruta: todas las entregas pasan a En traslado
//    @PatchMapping("/ruta/iniciar")
//    public ResponseEntity<List<EntregaDTO>> iniciarRuta(
//            @RequestParam List<String> entregaIds,
//            @RequestParam String choferId) {
//        try {
//            return ResponseEntity.ok(entregaService.iniciarRuta(entregaIds, choferId));
//        } catch (IllegalStateException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
    @GetMapping("/entregas")
    public ResponseEntity<List<EntregaDTO>> listarEntregas() {
        return ResponseEntity.ok(entregaService.listarEntregas());
    }

    @GetMapping("entregasDeEntidad/{idEntidadBeneficiaria}")
    public ResponseEntity<List<EntregaDTO>> listarEntregas(@PathVariable Long idEntidadBeneficiaria) {
        return ResponseEntity.ok(entregaService.listarEntregasDeEntidadBeneficiaria(idEntidadBeneficiaria));
    }


    @GetMapping("/entrega/{id}")
    public ResponseEntity<EntregaDTO> obtenerEntrega(@PathVariable String id) {
        try {
            return ResponseEntity.ok(entregaService.obtenerEntrega(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}