package ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RankingPorAlgoritmo {
    private Long id;
    private String nombreAlgoritmo;
    private List<Necesidad> necesidades = new ArrayList<>();
}
