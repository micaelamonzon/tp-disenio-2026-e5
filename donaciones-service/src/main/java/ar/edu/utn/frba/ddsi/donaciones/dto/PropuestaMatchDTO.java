package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.util.List;

public record PropuestaMatchDTO(Long id, Long donacionId, Long necesidadSeleccionadaId,
                                List<NecesidadRankingDTO> rankingConjunto,
                                List<RankingAlgoritmoDTO> rankingsIndividuales) {}
