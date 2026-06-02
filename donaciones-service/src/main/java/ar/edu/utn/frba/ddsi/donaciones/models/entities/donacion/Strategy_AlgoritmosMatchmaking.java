package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;

public interface Strategy_AlgoritmosMatchmaking {

    double calcularPuntaje(Donacion donacion, Necesidad necesidad);
}
