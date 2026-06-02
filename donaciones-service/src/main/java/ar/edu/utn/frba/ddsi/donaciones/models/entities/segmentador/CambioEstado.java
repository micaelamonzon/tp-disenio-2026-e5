package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CambioEstado {
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fecha;
    private String justificacion;

    public CambioEstado(EstadoDonacion anterior, EstadoDonacion nuevo, String justificacion) {
        this.estadoAnterior = anterior != null ? anterior.getNombreEstado() : "INICIO";
        this.estadoNuevo = nuevo.getNombreEstado();
        this.fecha = LocalDateTime.now();
        this.justificacion = justificacion;
    }
}