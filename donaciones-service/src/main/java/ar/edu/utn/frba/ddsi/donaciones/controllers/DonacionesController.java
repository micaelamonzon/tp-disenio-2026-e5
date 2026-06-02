package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaJuridicaDTO;
import ar.edu.utn.frba.ddsi.donaciones.services.DonacionesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")   //llamar desde postman o google como -> localhost:8080/servicioDeDonaciones/saludar
public class DonacionesController {

    private final DonacionesService donacionesService;

    public DonacionesController(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    // ===================== ENDPOINTS GENERALES =====================

    @GetMapping("/saludar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String primerEndPoint(){
        return "Hola desde el servicio de Donaciones!!";
    }

    @GetMapping("/obtenerDonantes")
    public ResponseEntity<List<PersonaDonanteDTO>> obtenerTodosLosDonantesUnificados() {
        return ResponseEntity.ok(donacionesService.obtenerTodosLosDonantesUnificados());
    }

    // Donante humano

    @PostMapping("/donantes/humano")
    public ResponseEntity<PersonaHumanaDTO> crearDonanteHumano(@RequestBody PersonaHumanaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donacionesService.crearDonanteHumano(request));
    }

    @GetMapping("/donantes/humano")
    public ResponseEntity<List<PersonaDonanteDTO>> obtenerDonantesHumanos() {
        return ResponseEntity.ok(donacionesService.obtenerTodosHumanos());
    }

    @GetMapping("/donantes/humano/{id}")
    public ResponseEntity<PersonaHumanaDTO> obtenerDonanteHumanoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(donacionesService.obtenerHumanoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/donantes/humano/{id}")
    public ResponseEntity<PersonaHumanaDTO> modificarDonanteHumano(
            @PathVariable Long id,
            @RequestBody PersonaHumanaDTO request) {
        return ResponseEntity.ok(donacionesService.modificarDonanteHumano(id, request));
    }

    @DeleteMapping("/donantes/humano/{id}")
    public ResponseEntity<Void> eliminarDonanteHumano(@PathVariable Long id) {
        donacionesService.eliminarDonanteHumano(id);
        return ResponseEntity.noContent().build();
    }

    // Donante Juridico

    @PostMapping("/donantes/juridico")
    public ResponseEntity<PersonaJuridicaDTO> crearDonanteJuridico(@RequestBody PersonaJuridicaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donacionesService.crearDonanteJuridico(request));
    }

    @GetMapping("/donantes/juridico")
    public ResponseEntity<List<PersonaDonanteDTO>> obtenerDonantesJuridicos() {
        return ResponseEntity.ok(donacionesService.obtenerTodosJuridicos());
    }

    @GetMapping("/donantes/juridico/{id}")
    public ResponseEntity<PersonaJuridicaDTO> obtenerDonanteJuridicoPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(donacionesService.obtenerJuridicoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/donantes/juridico/{id}")
    public ResponseEntity<PersonaJuridicaDTO> modificarDonanteJuridico(
            @PathVariable Long id,
            @RequestBody PersonaJuridicaDTO request) {
        return ResponseEntity.ok(donacionesService.modificarDonanteJuridico(id, request));
    }

    @DeleteMapping("/donantes/juridico/{id}")
    public ResponseEntity<Void> eliminarDonanteJuridico(@PathVariable Long id) {
        donacionesService.eliminarDonanteJuridico(id);
        return ResponseEntity.noContent().build();
    }

    // Donaciones de donante humano

    @PostMapping("/donaciones/humano/{idDonante}")
    public ResponseEntity<DonacionSinSegmentarDTO> crearDonacionDeHumano(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long idDonante) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(donacionesService.crearDonacionDeHumano(request, idDonante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/donaciones/humano/{idDonante}")
    public ResponseEntity<PersonaDonanteDTO> obtenerDonacionesDeHumano(@PathVariable Long idDonante) {
        try {
            return ResponseEntity.ok(donacionesService.obtenerDonacionesDeHumano(idDonante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/donaciones/humano/{idDonante}/{idDonacion}/{idBien}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> modificarDonacionDeHumano(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long idDonante,
            @PathVariable Long idDonacion,
            @PathVariable Long idBien) {
        try {
            return ResponseEntity.ok(
                    donacionesService.modificarDonacionDeHumano(request, idDonante, idDonacion, idBien));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/donaciones/humano/{idDonante}/{idDonacion}")
    public ResponseEntity<Void> eliminarDonacionDeHumano(
            @PathVariable Long idDonante,
            @PathVariable Long idDonacion) {
        try {
            donacionesService.eliminarDonacionDeHumano(idDonante, idDonacion);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Donaciones de donante juridico

    @PostMapping("/donaciones/juridico/{idDonante}")
    public ResponseEntity<DonacionSinSegmentarDTO> crearDonacionDeJuridico(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long idDonante) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(donacionesService.crearDonacionDeJuridico(request, idDonante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/donaciones/juridico/{idDonante}")
    public ResponseEntity<PersonaDonanteDTO> obtenerDonacionesDeJuridico(@PathVariable Long idDonante) {
        try {
            return ResponseEntity.ok(donacionesService.obtenerDonacionesDeJuridico(idDonante));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/donaciones/juridico/{idDonante}/{idDonacion}/{idBien}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> modificarDonacionDeJuridico(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long idDonante,
            @PathVariable Long idDonacion,
            @PathVariable Long idBien) {
        try {
            return ResponseEntity.ok(
                    donacionesService.modificarDonacionDeJuridica(request, idDonante, idDonacion, idBien));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/donaciones/juridico/{idDonante}/{idDonacion}")
    public ResponseEntity<Void> eliminarDonacionDeJuridico(
            @PathVariable Long idDonante,
            @PathVariable Long idDonacion) {
        try {
            donacionesService.eliminarDonacionDeJuridico(idDonante, idDonacion);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}