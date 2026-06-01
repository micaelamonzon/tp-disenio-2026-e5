package ar.edu.utn.frba.ddsi.dto;
import ar.edu.utn.frba.ddsi.models.entities.persona.MetricaMensual;
import java.time.YearMonth;
import java.util.List;

public record MetricasImpactoDTO(
        Long idDonante,
        String nombreDonante,
        int totalDonacionesHistoricas,
        YearMonth mesConMasDonaciones,
        long cantidadDonacionesEnMesPico,

        // Evolucion mes a mes
        List<MetricaMensual> evolucionMensual,
        int comparacionConMesAnterior,
        int totalOrganizacionesAyudadas,

        // Ranking
        Integer posicionEnRanking
) {}