package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RankingMensual {
    private LocalDate fecha;
    private Donante primerPuesto;
    private Donante segundoPuesto;
    private Donante tercerPuesto;

    public RankingMensual(LocalDate fecha, Donante primerPuesto, Donante segundoPuesto, Donante tercerPuesto){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
    }

    public Integer getPosicion(Donante donante) {
    }
}
