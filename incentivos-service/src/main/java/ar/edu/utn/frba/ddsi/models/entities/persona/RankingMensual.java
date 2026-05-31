package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RankingMensual {
    private LocalDate fecha;
    private PersonaDonanteDTO primerPuesto;
    private PersonaDonanteDTO segundoPuesto;
    private PersonaDonanteDTO tercerPuesto;

    public RankingMensual(LocalDate fecha, PersonaDonanteDTO primerPuesto, PersonaDonanteDTO segundoPuesto, PersonaDonanteDTO tercerPuesto){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
    }
}
