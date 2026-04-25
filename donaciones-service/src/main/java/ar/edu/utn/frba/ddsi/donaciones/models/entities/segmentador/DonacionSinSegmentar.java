package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.common.Donante.Donante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;

import java.util.ArrayList;

public class DonacionSinSegmentar {

  private Donante donante;
  private ArrayList<Bien> bienes = new ArrayList<>();

    public void DonacionSinSegmentar(Donante donante) {
        this.donante = donante;
    }

}
