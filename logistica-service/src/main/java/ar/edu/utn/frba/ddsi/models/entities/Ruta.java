package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "rutas")
public class Ruta {
    @Id
    private String id;
    private String camionId;
    private LocalDate fecha;
    private List<PuntosDeEntrega> puntosDeEntrega;
    private LocalTime horaEstimadaInicio;
    private LocalTime horaEstimadaFin;
    private Integer distanciaTotal;
    private Integer duracionTotalMins;
}