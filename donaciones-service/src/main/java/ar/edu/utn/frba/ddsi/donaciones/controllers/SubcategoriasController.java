package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.repositories.SubcategoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

    // catalogo agrupado por categoría para el formulario de carga del admin
    // Así el admin y las entidades cuando registran necesidades usan los mismos nombres,
    // que es lo que compara el matchmaking
    @GetMapping("/porCategoria")
    public ResponseEntity<Map<String, List<String>>> obtenerPorCategoria() {
        return ResponseEntity.ok(
                subcategoriaRepository.findAll().stream()
                        .filter(s -> s.getCategoria() != null && s.getCategoria().getNombre() != null)
                        .collect(Collectors.groupingBy(
                                s -> s.getCategoria().getNombre(),
                                TreeMap::new,
                                Collectors.mapping(Subcategoria::getNombre,
                                        Collectors.collectingAndThen(Collectors.toList(),
                                                lista -> lista.stream().distinct().sorted().toList())))));
    }
}
