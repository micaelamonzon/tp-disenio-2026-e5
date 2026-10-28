package ar.edu.utn.frba.ddsi.dtos.donaciones.misiones;

import java.time.LocalDate;

public record RankingMensualDTO(
        LocalDate fecha,
        String primerPuesto,
        String segundoPuesto,
        String tercerPuesto,
        Long misionesPrimerPuesto,
        Long misionesSegundoPuesto,
        Long misionesTercerPuesto
) {
}
