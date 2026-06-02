package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity //Avisa que la clase se va a convertir en tabla en BD
@Data
public class RankingPorAlgoritmo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreAlgoritmo;

    @ManyToMany
    @OrderColumn(name = "orden_necesidad") //Para asegurar que se mantenga orden del ranking

    private List<Necesidad> necesidades = new ArrayList<>();
}
