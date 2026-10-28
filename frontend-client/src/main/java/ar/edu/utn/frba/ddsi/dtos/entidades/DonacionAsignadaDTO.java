package ar.edu.utn.frba.ddsi.dtos.entidades;

import ar.edu.utn.frba.ddsi.dtos.donaciones.BienDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Getter
@Setter
public class DonacionAsignadaDTO {
    private Long id;
    private String estadoActual;
    private Integer cantidad;
    private LocalDateTime fechaDeAsignacion;
    private String subcategoria;
    private String descripcion;
    private Boolean donacionEntregada;

    public String Hace() {
        LocalDateTime base = fechaDeAsignacion != null ? fechaDeAsignacion : null;
        if (base == null) return "";
        long dias = ChronoUnit.DAYS.between(base, LocalDateTime.now());
        if (dias < 1) {
            return "Hoy";
        }
        if (dias == 1){
            return "Hace 1 día";
        }
        if (dias < 7){
            return "Hace " + dias + " días";
        }
        if (dias < 30) {
            long semanas = dias / 7;
            return semanas == 1 ? "Hace 1 semana" : "Hace " + semanas + " semanas";
        }
        long meses = dias / 30;
        return meses == 1 ? "Hace 1 mes" : "Hace " + meses + " meses";
    }

}
