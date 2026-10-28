package ar.edu.utn.frba.ddsi.dtos.donaciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropuestaMatchDTO {
    private Long id;
    private Long donacionId;
    private Long necesidadSeleccionadaId;
    private List<NecesidadRankingDTO> rankingConjunto = new ArrayList<>();
    private List<RankingAlgoritmoDTO> rankingsIndividuales = new ArrayList<>();
}