package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PropuestaMatch extends Persistente {
    private DonacionSegmentada donacion; // donación a la que corresponden los rankings
    private List<Necesidad> rankingConjunto = new ArrayList<>(); //Cuando hay coincidencias
    private Necesidad necesidadSeleccionada;

    // Guarda los rankings separados si no hubo coincidencias globales

    private List<RankingPorAlgoritmo> rankingsIndividuales = new ArrayList<>();

}
