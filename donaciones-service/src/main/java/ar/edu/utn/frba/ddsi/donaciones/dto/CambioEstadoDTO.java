package ar.edu.utn.frba.ddsi.donaciones.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CambioEstadoDTO {
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fecha;
    private String justificacion;
    private String responsableId;

    public CambioEstadoDTO(String estadoAnterior, String estadoNuevo, LocalDateTime fecha, String justificacion) {

    }

    public void setEstadoAnterior(String estadoAnterior) {
    }
}