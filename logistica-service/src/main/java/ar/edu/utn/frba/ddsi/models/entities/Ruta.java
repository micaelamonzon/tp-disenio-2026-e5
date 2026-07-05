package ar.edu.utn.frba.ddsi.models.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Ruta {
    public String id;
    public LocalDate fecha;
    public List<PuntosDeEntrega> puntosDeEntrega;
    public LocalTime horaEstimadaInicio;
    public LocalTime horaEstimadaFin;
    public Integer distanciaTotal;
    public Integer duracionTotalMins;
}
