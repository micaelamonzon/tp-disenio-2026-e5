package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public interface Strategy_AlgoritmosMatchmaking {

    double calcularPuntaje(DonacionSegmentada donacion, Necesidad necesidad);
}
