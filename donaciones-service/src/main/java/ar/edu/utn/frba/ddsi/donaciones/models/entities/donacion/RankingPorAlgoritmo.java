package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RankingPorAlgoritmo {
    private Long id;
    private String nombreAlgoritmo;
    private List<Necesidad> necesidades = new ArrayList<>();
}
