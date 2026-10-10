package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.donaciones.services.EntidadBeneficiariaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")
public class EntidadBeneficiariaController {

    private final EntidadBeneficiariaService entidadBeneficiariaService;

    public EntidadBeneficiariaController(EntidadBeneficiariaService entidadBeneficiariaService) {
        this.entidadBeneficiariaService = entidadBeneficiariaService;
    }

    @GetMapping("/entidades")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<EntidadBeneficiariaDTO> obtenerTodas() {
        return this.entidadBeneficiariaService.obtenerTodas();
    }

    @GetMapping("/entidad/{id}")
    public ResponseEntity<EntidadBeneficiariaDTO> obtenerPorId(@PathVariable Long id) {
        try {
            EntidadBeneficiariaDTO entidad = this.entidadBeneficiariaService.obtenerPorId(id);
            return ResponseEntity.ok(entidad);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("entidad/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EntidadBeneficiariaDTO modificar(@RequestBody EntidadBeneficiariaDTO body, @PathVariable Long id){
        return this.entidadBeneficiariaService.modificar(body,id);
    }
    @PostMapping("/entidad")
    @ResponseStatus(HttpStatus.CREATED)
    public EntidadBeneficiariaDTO crear(@RequestBody EntidadBeneficiariaDTO body) {
        return this.entidadBeneficiariaService.crear(body);
    }

    @DeleteMapping("/entidad/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            this.entidadBeneficiariaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}