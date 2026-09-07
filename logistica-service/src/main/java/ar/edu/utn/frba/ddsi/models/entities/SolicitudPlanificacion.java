package ar.edu.utn.frba.ddsi.models.entities;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "solicitudes_planificacion")
public class SolicitudPlanificacion {

    @Getter
    @Id
    private String requestId;

    @Getter
    private EstadoPlanificacion estado;

    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;

    @Getter
    private String resultadoJson;

    @Getter
    private String mensajeError;

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
