package ar.edu.utn.frba.ddsi.dtos.donaciones;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public String getCategoria() {
        if (bienes == null || bienes.isEmpty() || bienes.get(0).getSubcategoria() == null
                || bienes.get(0).getSubcategoria().getCategoria() == null) return null;
        return bienes.get(0).getSubcategoria().getCategoria().getNombre();
    }
}


