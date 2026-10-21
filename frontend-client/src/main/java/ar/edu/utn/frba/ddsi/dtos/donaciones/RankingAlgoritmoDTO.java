package ar.edu.utn.frba.ddsi.dtos.donaciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingAlgoritmoDTO {
    private String algoritmo;
    private List<NecesidadRankingDTO> necesidades = new ArrayList<>();
}