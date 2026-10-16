package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.NombreDeMision;

public record MisionFrontDTO(
        Long id,
        String  nombre,
        String categoria,
        Integer ordenSecuencial,
        String insigniaGanadora,
        Integer meses,
        String mesInicial,
        String mesFinal,
        Integer objetivoDeCategoriasDistintas,
        Integer cantidadDeDonaciones,
        Integer cantidadDeBienes,
        EstadoDeMision estado,
        Integer progreso,
        Integer distanciaDelObjetivo
) {
}
