package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CambioEstado {
    private Long id;
    private EstadoDonacion estadoAnterior;
    private EstadoDonacion estadoNuevo;
    private LocalDateTime fecha;
    private String justificacion;    // el "por qué"
    private String responsableId;    // el "quién" — puede ser userId o nombre
    private TipoDonante tipoDonante;
    public CambioEstado(EstadoDonacion anterior, EstadoDonacion nuevo,
                        String justificacion, String responsableId) {
        this.estadoAnterior = anterior != null ? anterior : EstadoDonacion.INICIO;
        this.estadoNuevo = nuevo;
        this.fecha = LocalDateTime.now();
        this.justificacion = justificacion;
        this.responsableId = responsableId;
    }
}