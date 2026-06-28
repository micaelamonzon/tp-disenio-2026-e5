package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CambioEstado {
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fecha;
    private String justificacion;    // el "por qué"
    private String responsableId;    // el "quién" — puede ser userId o nombre

    public CambioEstado(EstadoDonacion anterior, EstadoDonacion nuevo,
                        String justificacion, String responsableId) {
        this.estadoAnterior = anterior != null ? anterior.name() : "INICIO";
        this.estadoNuevo = nuevo.name();
        this.fecha = LocalDateTime.now();
        this.justificacion = justificacion;
        this.responsableId = responsableId;
    }
}