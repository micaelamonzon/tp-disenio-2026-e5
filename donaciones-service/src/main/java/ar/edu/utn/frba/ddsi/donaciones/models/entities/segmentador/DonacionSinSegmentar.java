package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import lombok.Data;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
public class DonacionSinSegmentar {

  private List<Bien> bienes = new ArrayList<>();
  private List<DonacionSegmentada> donacionesSegmentadas = new ArrayList<>();
  LocalDateTime fechaDeIngreso;

  public void agregarBien(Bien bien){
    this.bienes.add(bien);
  }
  public void agregarDonacionSegmentada(DonacionSegmentada donacion) {
    this.donacionesSegmentadas.add(donacion);
  }
}
