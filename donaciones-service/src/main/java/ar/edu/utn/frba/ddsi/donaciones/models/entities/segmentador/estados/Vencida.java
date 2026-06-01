package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;

public class Vencida extends EstadoDonacion {
    @Override public String getNombreEstado() { return "VENCIDA"; }
}