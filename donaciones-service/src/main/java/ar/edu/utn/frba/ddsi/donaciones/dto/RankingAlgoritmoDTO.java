package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.util.List;

public record RankingAlgoritmoDTO(String algoritmo, List<NecesidadRankingDTO> necesidades) {}
