package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public class ListaParaEntregar extends EstadoDonacion {
    @Override public String getNombreEstado() { return "LISTA_PARA_ENTREGAR"; }

    @Override
    public void iniciarTraslado(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new EnTraslado(), "Camión inició recorrido");
    }

    @Override
    public void marcarVencida(DonacionSegmentada donacion) {
        donacion.cambiarEstado(new Vencida(), "Marcada como vencida por administrador");
    }
}