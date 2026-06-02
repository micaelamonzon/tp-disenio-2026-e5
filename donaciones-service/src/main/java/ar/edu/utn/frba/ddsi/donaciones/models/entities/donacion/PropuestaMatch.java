package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import jakarta.persistence.*;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class PropuestaMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Donacion donacion; // donación a la que corresponden los rankings

    @ManyToMany
    @OrderColumn(name = "orden_conjunto")
    private List<Necesidad> rankingConjunto = new ArrayList<>();

    // Guarda los rankings separados si no hubo coincidencias globales
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "propuesta_match_id") // Clave foránea en la tabla de rankings individuales
    private List<RankingPorAlgoritmo> rankingsIndividuales = new ArrayList<>();
}
