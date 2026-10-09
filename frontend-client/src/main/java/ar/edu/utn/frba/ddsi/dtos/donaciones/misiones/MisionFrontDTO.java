package ar.edu.utn.frba.ddsi.dtos.donaciones.misiones;

public record MisionFrontDTO(
        Long id,
        String  nombre,
        String categoria,
        Integer ordenSecuencial,
        String insigniaGanadora,
        Integer meses,
        Integer objetivoDeCategoriasDistintas,
        Integer cantidadDeDonaciones,
        Integer cantidadDeBienes,
        EstadoDeMision estado,
        Integer progreso,
        Integer distanciaDelObjetivo
) {
}
