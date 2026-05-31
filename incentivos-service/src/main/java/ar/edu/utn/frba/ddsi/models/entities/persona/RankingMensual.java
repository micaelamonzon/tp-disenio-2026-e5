package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RankingMensual {
    private LocalDate fecha;
    private PersonaHumana primerPuesto;
    private PersonaHumana segundoPuesto;
    private PersonaHumana tercerPuesto;

    public RankingMensual(LocalDate fecha, PersonaHumana primerPuesto, PersonaHumana segundoPuesto, PersonaHumana tercerPuesto){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
    }
}
