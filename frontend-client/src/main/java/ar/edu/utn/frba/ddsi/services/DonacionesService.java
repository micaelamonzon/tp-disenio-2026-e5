package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DonacionesService {

    private final DonacionesApiService donacionesApiService;

    public DonacionesService(DonacionesApiService donacionesApiService) {
        this.donacionesApiService = donacionesApiService;
    }

    public List<DonacionDTO> obtenerDonacionesDeHumano(Long idDonante) {
        return donacionesApiService.obtenerDonacionesDeHumano(idDonante);
    }

    public List<DonacionDTO> filtrar(List<DonacionDTO> donaciones,
                                     String estado, String categoria, String subcategoria) {
        return donaciones.stream()
                .filter(d -> vacio(estado) || estado.equals(d.getEstadoActual()))
                .filter(d -> vacio(categoria) || categoria.equals(d.getCategoria()))
                .filter(d -> vacio(subcategoria) || subcategoria.equals(d.getSubcategoria()))
                .toList();
    }

    public List<String> categorias(List<DonacionDTO> donaciones) {
        return donaciones.stream().map(DonacionDTO::getCategoria)
                .filter(Objects::nonNull).distinct().sorted().toList();
    }

    /** Si hay categoría elegida, solo sus subcategorías (filtro dependiente). */
    public List<String> subcategorias(List<DonacionDTO> donaciones, String categoria) {
        return donaciones.stream()
                .filter(d -> vacio(categoria) || categoria.equals(d.getCategoria()))
                .map(DonacionDTO::getSubcategoria)
                .filter(Objects::nonNull).distinct().sorted().toList();
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
