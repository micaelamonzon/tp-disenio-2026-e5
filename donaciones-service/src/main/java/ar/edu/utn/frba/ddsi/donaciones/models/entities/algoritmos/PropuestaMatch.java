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
    @ManyToOne
    @JoinColumn(name = "donacion_id")
    private DonacionSegmentada donacion;

    // Cuando hay coincidencias entre algoritmos. El orden importa (primero el mejor)
    @ManyToMany
    @JoinTable(name = "propuesta_ranking_conjunto",
            joinColumns = @JoinColumn(name = "propuesta_id"),
            inverseJoinColumns = @JoinColumn(name = "necesidad_id"))
    @OrderColumn(name = "posicion")
    private List<Necesidad> rankingConjunto = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "necesidad_seleccionada_id")
    private Necesidad necesidadSeleccionada;

    // Rankings separados si no hubo coincidencias globales
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "propuesta_id")
    private List<RankingPorAlgoritmo> rankingsIndividuales = new ArrayList<>();

}
