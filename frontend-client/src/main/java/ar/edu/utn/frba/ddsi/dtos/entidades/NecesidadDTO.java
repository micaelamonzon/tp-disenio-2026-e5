package ar.edu.utn.frba.ddsi.dtos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NecesidadDTO {
    private Long id;
    private String tipo;            // RECURRENTE / EXTRAORDINARIA
    private String subcategoria;
    private String descripcion;
    private boolean estaSatisfecha;
    private int cantidad;           // objetivo por período o cantidad requerida
    private String periodo;         // DIAS / SEMANAS / MESES (null si es extraordinaria)



    public boolean esExtraordinaria() {
        return "EXTRAORDINARIA".equals(tipo);
    }

    public String getTipoLegible() {
        return esExtraordinaria() ? "Extraordinaria" : "Recurrente";
    }

    public String getPeriodoLegible() {
        if (periodo == null) return "";
        return switch (periodo) {
            case "DIAS" -> "por día";
            case "SEMANAS" -> "por semana";
            case "MESES" -> "por mes";
            default -> periodo.toLowerCase();
        };
    }
}
