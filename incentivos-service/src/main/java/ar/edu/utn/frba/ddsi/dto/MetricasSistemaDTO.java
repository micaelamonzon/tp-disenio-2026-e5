package ar.edu.utn.frba.ddsi.dto;
import java.util.List;

public record MetricasSistemaDTO(

        // Donantes
        int totalDonantesActivos,
        int donantesNuevosEsteMes,

        // Donaciones
        int donacionesRecibidasEsteMes,
        int donacionesEntregadasEsteMes,
        int donacionesPendientes,
        int comparacionDonacionesConMesAnterior,

        // Misiones
        int misionesCompletadasEsteMes,

        // Ranking top 3
        List<String> top3Donantes
) {}