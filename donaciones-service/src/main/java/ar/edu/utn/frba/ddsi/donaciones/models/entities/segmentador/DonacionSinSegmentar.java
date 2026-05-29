package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import lombok.Data;

import java.util.ArrayList;

@Data
public class DonacionSinSegmentar {

  private ArrayList<Bien> bienes = new ArrayList<>();
  private ArrayList<DonacionSegmentada> donacionesSegmentadas = new ArrayList<>();

  public void agregarBien(Bien bien){
    this.bienes.add(bien);
  }
  public void agregarDonacionSegmentada(DonacionSegmentada donacion) {
    this.donacionesSegmentadas.add(donacion);
  }
}
