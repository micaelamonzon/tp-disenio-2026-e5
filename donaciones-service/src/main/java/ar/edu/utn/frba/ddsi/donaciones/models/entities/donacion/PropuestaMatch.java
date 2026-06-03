package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


@Data
public class PropuestaMatch {
    private Long id;
    private Donacion donacion; // donación a la que corresponden los rankings
    private List<Necesidad> rankingConjunto = new ArrayList<>(); //Cuando hay coincidencias
    private Necesidad necesidadSeleccionada;

    // Guarda los rankings separados si no hubo coincidencias globales

    private List<RankingPorAlgoritmo> rankingsIndividuales = new ArrayList<>();

}
