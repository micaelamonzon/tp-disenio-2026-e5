package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Categoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.repositories.CategoriaRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.SubcategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CatalogoSubcategoriasService {

    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;

    public CatalogoSubcategoriasService(SubcategoriaRepository subcategoriaRepository,
                                        CategoriaRepository categoriaRepository) {
        this.subcategoriaRepository = subcategoriaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Devuelve la subcategoría con ese nombre o la crea si no existe.
     * Las necesidades solo informan el nombre de la subcategoría, así que pueden
     * crearla sin categoría; la primera donación que la use le completa la categoría.
     */
    public Subcategoria obtenerOCrear(String nombre, Boolean esPerecedero, String nombreCategoria) {
        Categoria categoria = (nombreCategoria == null || nombreCategoria.isBlank())
                ? null
                : obtenerOCrearCategoria(nombreCategoria.trim());

        return subcategoriaRepository.findByNombreIgnoreCase(nombre.trim())
                .map(existente -> {
                    if (existente.getCategoria() == null && categoria != null) {
                        existente.setCategoria(categoria);
                        return subcategoriaRepository.save(existente);
                    }
                    return existente;
                })
                .orElseGet(() -> subcategoriaRepository.save(
                        new Subcategoria(nombre.trim(), Boolean.TRUE.equals(esPerecedero), categoria)));
    }

    private Categoria obtenerOCrearCategoria(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> categoriaRepository.save(new Categoria(nombre)));
    }
}
