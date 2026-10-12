package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.repositories.SubcategoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones/subcategorias")
public class SubcategoriasController {
    private SubcategoriaRepository subcategoriaRepository;

    public SubcategoriasController(SubcategoriaRepository subcategoriaRepository) {
        this.subcategoriaRepository = subcategoriaRepository;
    }

    @GetMapping("/todas")
    public ResponseEntity<List<String>> obtenerSubcategorias() {
        return ResponseEntity.ok(
                subcategoriaRepository.findAll().stream()
                        .map(Subcategoria::getNombre)
                        .sorted()
                        .toList());
    }
}
