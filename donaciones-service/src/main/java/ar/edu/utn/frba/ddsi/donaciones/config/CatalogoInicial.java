package ar.edu.utn.frba.ddsi.donaciones.config;

import ar.edu.utn.frba.ddsi.donaciones.services.CatalogoSubcategoriasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Carga inicial del catálogo de categorías y subcategorías al arrancar donaciones.
// Para agregar opciones nuevas, sumarlas aca
@Component
public class CatalogoInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogoInicial.class);

    // Categoría -> subcategorías (las de admin.js + algunas comunes y los ejemplos del TP)
    private static final Map<String, List<String>> CATALOGO = new LinkedHashMap<>();
    static {
        CATALOGO.put("Alimentos", List.of("Fideos secos", "Arroz", "Legumbres secas", "Aceite vegetal",
                "Leche en polvo", "Galletitas", "Conservas", "Harina", "Azúcar", "Yerba", "Frutas", "Verduras"));
        // "Ropa de abrigo" (ejemplo del TP) incluye camperas: el detalle va en la descripción del bien
        CATALOGO.put("Vestimenta", List.of("Ropa de abrigo", "Remeras", "Pantalones",
                "Ropa infantil", "Calzado"));
        CATALOGO.put("Mobiliario", List.of("Sillas", "Mesas", "Estantes", "Camas"));
        CATALOGO.put("Abrigo", List.of("Frazadas", "Colchones"));
    }

    // Las mismas que en admin.js (CATEGORIAS_PERECEDERAS)
    private static final Set<String> CATEGORIAS_PERECEDERAS = Set.of("Alimentos");

    private final CatalogoSubcategoriasService catalogo;

    public CatalogoInicial(CatalogoSubcategoriasService catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public void run(ApplicationArguments args) {
        int cantidad = 0;
        for (Map.Entry<String, List<String>> categoria : CATALOGO.entrySet()) {
            boolean perecedera = CATEGORIAS_PERECEDERAS.contains(categoria.getKey());
            for (String subcategoria : categoria.getValue()) {
                catalogo.obtenerOCrear(subcategoria, perecedera, categoria.getKey());
                cantidad++;
            }
        }
        log.info("Catálogo inicial verificado: {} categorías, {} subcategorías", CATALOGO.size(), cantidad);
    }
}
