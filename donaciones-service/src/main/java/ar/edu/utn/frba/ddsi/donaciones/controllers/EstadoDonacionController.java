package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoRequestDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.services.EstadoDonacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servicioDeDonaciones")
public class EstadoDonacionController {

    @Autowired
    private final EstadoDonacionService estadoDonacionService;

    public EstadoDonacionController(EstadoDonacionService estadoDonacionService) {
        this.estadoDonacionService = estadoDonacionService;
    }

    @PostMapping("/donacion/registrar")
    public ResponseEntity<Long> registrar() {
        DonacionSegmentada nueva = new DonacionSegmentada(new Subcategoria("Arroz", false, null));
        Long id = estadoDonacionService.registrarDonacion(nueva);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/donacion/{id}/historial")
    public ResponseEntity<DonacionSegmentadaDTO> obtenerHistorial(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoDonacionService.obtenerHistorial(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/donacion/{id}/asignar")
    public ResponseEntity<DonacionSegmentadaDTO> asignar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoDonacionService.asignar(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/listaParaEntregar")
    public ResponseEntity<DonacionSegmentadaDTO> listaParaEntregar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoDonacionService.listaParaEntregar(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/iniciarTraslado")
    public ResponseEntity<DonacionSegmentadaDTO> iniciarTraslado(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoDonacionService.iniciarTraslado(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/entregar")
    public ResponseEntity<DonacionSegmentadaDTO> entregar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoDonacionService.entregar(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/fallarEntrega")
    public ResponseEntity<DonacionSegmentadaDTO> fallarEntrega(
            @PathVariable Long id,
            @RequestBody CambioEstadoDTO body) {
        try {
            return ResponseEntity.ok(estadoDonacionService.fallarEntrega(id, body.getJustificacion()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/vencer")
    public ResponseEntity<DonacionSegmentadaDTO> vencer(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoDonacionService.vencer(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
