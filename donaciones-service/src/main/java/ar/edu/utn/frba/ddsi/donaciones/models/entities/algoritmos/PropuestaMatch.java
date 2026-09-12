package ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "PropuestaMatch")
public class PropuestaMatch extends Persistente {
    @Transient
    private DonacionSegmentada donacion; // donación a la que corresponden los rankings
    @Transient
    private List<Necesidad> rankingConjunto = new ArrayList<>(); //Cuando hay coincidencias
    @Transient
    private Necesidad necesidadSeleccionada;

    // Guarda los rankings separados si no hubo coincidencias globales

    @Transient
    private List<RankingPorAlgoritmo> rankingsIndividuales = new ArrayList<>();

}
