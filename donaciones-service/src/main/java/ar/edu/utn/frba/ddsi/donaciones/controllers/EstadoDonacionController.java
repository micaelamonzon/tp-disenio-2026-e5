package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoRequestDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.services.EstadoDonacionServiceimpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")
public class EstadoDonacionController {

    private final EstadoDonacionServiceimpl estadoDonacionService; // ← cambia acá

    public EstadoDonacionController(EstadoDonacionServiceimpl estadoDonacionService) { // ← y acá
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
    public ResponseEntity<DonacionSegmentadaDTO> asignar(
            @PathVariable Long id,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(estadoDonacionService.asignar(id, responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/listaParaEntregar")
    public ResponseEntity<DonacionSegmentadaDTO> listaParaEntregar(
            @PathVariable Long id,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(estadoDonacionService.listaParaEntregar(id, responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/iniciarTraslado")
    public ResponseEntity<DonacionSegmentadaDTO> iniciarTraslado(
            @PathVariable Long id,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(estadoDonacionService.iniciarTraslado(id, responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/entregar")
    public ResponseEntity<DonacionSegmentadaDTO> entregar(
            @PathVariable Long id,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(estadoDonacionService.entregar(id, responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/fallarEntrega")
    public ResponseEntity<DonacionSegmentadaDTO> fallarEntrega(
            @PathVariable Long id,
            @RequestParam String responsableId,
            @RequestBody CambioEstadoRequestDTO body) {
        try {
            return ResponseEntity.ok(estadoDonacionService.fallarEntrega(
                    id, body.getJustificacion(), responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/donacion/{id}/vencer")
    public ResponseEntity<DonacionSegmentadaDTO> vencer(
            @PathVariable Long id,
            @RequestParam String responsableId) {
        try {
            return ResponseEntity.ok(estadoDonacionService.vencer(id, responsableId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Para que logística pueda consultar donaciones según estado
    @GetMapping("/donaciones")
    public ResponseEntity<List<DonacionSegmentada>> buscarPorEstado(@RequestParam String estado) {
        List<DonacionSegmentada> encontradas = estadoDonacionService.findByEstado(estado);
        return ResponseEntity.ok(encontradas);
    }


}
