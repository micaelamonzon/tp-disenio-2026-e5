package ar.edu.utn.frba.ddsi.dtos.donaciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DonacionDTO {
    private Long id;
    private String estadoActual;
    private List<BienDTO> bienes;
    private LocalDateTime fechaDeIngreso;
    private Boolean donacionEntregada;
    private Long organizacionId;

    public String getEstadoLegible() {
        if (estadoActual == null) return "";
        return switch (estadoActual) {
            case "EN_DEPOSITO" -> "En depósito";
            case "ASIGNACION_REALIZADA" -> "Asignación realizada";
            case "LISTA_PARA_ENTREGAR" -> "Lista para entregar";
            case "EN_TRASLADO" -> "En traslado";
            case "ENTREGADA" -> "Entregada";
            case "ENTREGA_FALLIDA" -> "Entrega fallida";
            case "VENCIDA" -> "Vencida";
            default -> estadoActual;
        };
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
}


