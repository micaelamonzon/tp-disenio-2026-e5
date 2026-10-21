package ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class RankingPorAlgoritmo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreAlgoritmo;

    @ManyToMany
    @JoinTable(name = "ranking_algoritmo_necesidad",
            joinColumns = @JoinColumn(name = "ranking_id"),
            inverseJoinColumns = @JoinColumn(name = "necesidad_id"))
    @OrderColumn(name = "posicion")
    private List<Necesidad> necesidades = new ArrayList<>();
}
