package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.services.EstadoDonacionServiceimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")
public class EstadoDonacionController {

    private static final Logger estadosDonacionController = LoggerFactory.getLogger(EstadoDonacionController.class);

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

    // El body con las fotos es opcional: si no viene, se confirma la entrega igual que antes
    @PatchMapping("/donacion/{id}/entregar")
    public ResponseEntity<DonacionSegmentadaDTO> entregar(
            @PathVariable Long id,
            @RequestParam String responsableId,
            @RequestBody(required = false) FotosEntregaDTO body) {
        try {
            List<String> fotosUrl = body != null ? body.fotosUrl() : null;
            return ResponseEntity.ok(estadoDonacionService.entregar(id, responsableId, fotosUrl));
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
    public ResponseEntity<List<DonacionDTO>> buscarPorEstado(@RequestParam String estado) {
        estadosDonacionController.info("ENTRE AL ENDPOINT DE DONACIONES BUSCAR POR ESTADO");
        List<DonacionDTO> encontradas = estadoDonacionService.findByEstado(estado);
        return ResponseEntity.ok(encontradas);
    }

    @GetMapping("/admin/donaciones")
    public List<DonacionAdminDTO> listarParaAdmin(@RequestParam String estado) {
        return estadoDonacionService.listarParaAdmin(estado);
    }

}
