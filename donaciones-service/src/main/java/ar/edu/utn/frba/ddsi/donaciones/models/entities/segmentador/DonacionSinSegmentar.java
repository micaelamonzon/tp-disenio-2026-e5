package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "donacion_sin_segmentar")
public class DonacionSinSegmentar extends Persistente {

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "donacionSinSegmentar_id", referencedColumnName = "id")
  private List<Bien> bienes = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "donacionSinSegmentar_id", referencedColumnName = "id")
  private List<DonacionSegmentada> donacionesSegmentadas = new ArrayList<>();

  @Column(name = "descripcion_general")
  private String descripcionGeneral;

  @Column(name = "fecha_de_ingreso")
  private LocalDateTime fechaDeIngreso;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="donanteHumano_id", referencedColumnName = "id")
  private PersonaHumana donanteHumano;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="donanteJuridico_id", referencedColumnName = "id")
  private PersonaJuridica donanteJuridico;



  public DonacionSinSegmentar(String descripcionGeneral, LocalDateTime fechaDeIngreso) {
    this.descripcionGeneral = descripcionGeneral;
    this.bienes = new ArrayList<>();
    this.fechaDeIngreso = fechaDeIngreso;
  }

  public void agregarBien(Bien bien){
    this.bienes.add(bien);
  }
  public void agregarDonacionSegmentada(DonacionSegmentada donacion) {
    this.donacionesSegmentadas.add(donacion);
  }

}
