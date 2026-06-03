package ar.edu.utn.frba.ddsi.donaciones.dto;

import lombok.Data;
import java.time.LocalDateTime;


public class CambioEstadoDTO {
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fecha;
    private String justificacion;
}