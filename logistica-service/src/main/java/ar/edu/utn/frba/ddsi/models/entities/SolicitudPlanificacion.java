package ar.edu.utn.frba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class SolicitudPlanificacion {

    @Getter
    @Id
    private String requestId;

    @Getter
    @Enumerated(EnumType.STRING)
    private EstadoPlanificacion estado;

    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;

    @Getter
    @Lob
    private String resultadoJson;

    @Getter
    private String mensajeError;

    protected SolicitudPlanificacion() {}

    public SolicitudPlanificacion(String requestId) {
        this.requestId = requestId;
        this.estado = EstadoPlanificacion.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    public void completar(String resultadoJson) {
        this.estado = EstadoPlanificacion.COMPLETADA;
        this.resultadoJson = resultadoJson;
        this.fechaRespuesta = LocalDateTime.now();
    }

    public void fallar(String mensajeError) {
        this.estado = EstadoPlanificacion.FALLIDA;
        this.mensajeError = mensajeError;
        this.fechaRespuesta = LocalDateTime.now();
    }
}
