package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.PosibleDonacion;

public class Entregada extends EstadoDonacion {
    @Override public String getNombreEstado() { return "ENTREGADA"; }
}