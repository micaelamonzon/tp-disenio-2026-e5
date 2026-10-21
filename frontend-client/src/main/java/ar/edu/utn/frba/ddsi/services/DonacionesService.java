package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DatosPersonalesDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.PropuestaMatchDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public List<DonacionDTO> obtenerDonacionesEntregadas() {
        return donacionesApiService.obtenerDonacionesEntregadas();
    }

    public List<DonacionDTO> obtenerDonacionesDeJuridico(Long idDonante) {
        return donacionesApiService.obtenerDonacionesDeJuridico(idDonante);
    }

    public List<DonacionDTO> obtenerDonacionesPorEstado(String estado) {
        return donacionesApiService.obtenerDonacionesPorEstado(estado);
    }

    public java.util.Map<String, List<String>> obtenerSubcategoriasPorCategoria() {
        return donacionesApiService.obtenerSubcategoriasPorCategoria();
    }

    public void vencerDonacion(Long id, String responsableId) {
        donacionesApiService.vencerDonacion(id, responsableId);
    }

    public void ejecutarMatchmaking() { donacionesApiService.ejecutarMatchmaking(); }

    public List<PropuestaMatchDTO> obtenerPropuestasPendientes() {
        return donacionesApiService.obtenerPropuestasPendientes();
    }

    public void seleccionarNecesidad(Long propuestaId, Long necesidadId, String responsableId) {
        donacionesApiService.seleccionarNecesidad(propuestaId, necesidadId, responsableId);
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

    public List<String> subcategorias(List<DonacionDTO> donaciones, String categoria) {
        return donaciones.stream()
                .filter(d -> vacio(categoria) || categoria.equals(d.getCategoria()))
                .map(DonacionDTO::getSubcategoria)
                .filter(Objects::nonNull).distinct().sorted().toList();
    }
    public List<EntidadBeneficiariaDTO> obtenerEntidades() {
        return donacionesApiService.obtenerEntidades();
    }

    public List<EntidadBeneficiariaDTO> buscarEntidades(
            List<EntidadBeneficiariaDTO> entidades, String texto) {
        if (vacio(texto)) {
            return entidades;
        }
        String t = texto.trim().toLowerCase();
        return entidades.stream()
                .filter(e -> contiene(e.getRazonSocial(), t)
                        || contiene(e.getDireccion(), t))
                .toList();
    }

    public EntidadBeneficiariaDTO obtenerEntidad(Long idEntidad) {
        return donacionesApiService.obtenerEntidad(idEntidad);
    }

    public DatosPersonalesDTO obtenerDatosPersonales(Long idDonante, boolean esHumano) {
        return donacionesApiService.obtenerDatosPersonales(idDonante, esHumano);
    }

    /** Primero las pendientes, despues las satisfechas*/
    public List<NecesidadDTO> obtenerNecesidades(Long idEntidad) {
        return donacionesApiService.obtenerNecesidades(idEntidad).stream()
                .sorted(Comparator.comparing(NecesidadDTO::isEstaSatisfecha))
                .toList();
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }
    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
