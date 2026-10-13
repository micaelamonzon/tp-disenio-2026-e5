package ar.edu.utn.frba.ddsi.dtos.donaciones;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DonacionDTO {

    public static final Map<String, String> ESTADOS;
    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("EN_DEPOSITO", "En depósito");
        m.put("ASIGNACION_REALIZADA", "Asignación realizada");
        m.put("LISTA_PARA_ENTREGAR", "Lista para entregar");
        m.put("EN_TRASLADO", "En traslado");
        m.put("ENTREGADA", "Entregada");
        m.put("ENTREGA_FALLIDA", "Entrega fallida");
        m.put("VENCIDA", "Vencida");
        ESTADOS = Collections.unmodifiableMap(m);
    }

    private Long id;
    private String estadoActual;
    private List<BienDTO> bienes;
    private LocalDateTime fechaDeIngreso;
    private Boolean donacionEntregada;
    private Long organizacionId;
    private String fotoPath;
    private String tipoDonante;
    private Long idDelDonante;
    private String nombreDonante;
    private Boolean puedeVencerse;

    public String getEstadoLegible() {
        if (estadoActual == null) return "";
        return ESTADOS.getOrDefault(estadoActual, estadoActual);
    }

    /** Reutiliza los colores de donante.css (.pill-Pendiente, .pill-Asignada, .pill-Recibida). */
    public String getEstadoCss() {
        if (estadoActual == null) return "";
        return switch (estadoActual) {
            case "EN_DEPOSITO" -> "pill-Pendiente";
            case "ASIGNACION_REALIZADA", "LISTA_PARA_ENTREGAR", "EN_TRASLADO" -> "pill-Asignada";
            case "ENTREGADA" -> "pill-Recibida";
            default -> "";
        };
    }

    /** Por la segmentación, todos los bienes de una donación comparten subcategoría. */
    public String getSubcategoria() {
        if (bienes == null || bienes.isEmpty() || bienes.get(0).getSubcategoria() == null) return null;
        return bienes.get(0).getSubcategoria().getNombre();
    }

    public String getArticulos() {
        if (bienes == null) return "";
        return bienes.stream().map(BienDTO::getNombre).filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    // Unidad del primer bien; es una aproximación si mezclan unidades
    public String getUnidad() {
        return (bienes == null || bienes.isEmpty() || bienes.get(0).getTipoUnidad() == null)
                ? "" : bienes.get(0).getTipoUnidad().toLowerCase();
    }

    // El vencimiento más próximo entre los bienes
    public LocalDateTime getVencimiento() {
        if (bienes == null) return null;
        return bienes.stream().map(BienDTO::getFechaDeVencimiento).filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);
    }


    public String getCategoria() {
        if (bienes == null || bienes.isEmpty() || bienes.get(0).getSubcategoria() == null
                || bienes.get(0).getSubcategoria().getCategoria() == null) return null;
        return bienes.get(0).getSubcategoria().getCategoria().getNombre();
    }

    // metodos para integracion galeria
    public int getCantidadTotalItems() {
        if (bienes == null || bienes.isEmpty()) return 0;
        return bienes.stream()
                .mapToInt(b -> b.getCantidad() != null ? b.getCantidad() : 1)
                .sum();
    } // utilizado para calcular que donaciones seran enviadas a la galeria, en este caso tomando en cuenta aquellas que tengan la mayor cantidad de bienes

    public String getImagenUrlEfectiva() {
        return (fotoPath != null && !fotoPath.isBlank()) ? fotoPath : null;
    }
}


