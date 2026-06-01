package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RankingMensual {
    private LocalDate fecha;
    private Donante primerPuesto;
    private Donante segundoPuesto;
    private Donante tercerPuesto;
    private List<Donante> rankingCompletoOrdenado;

    public RankingMensual(LocalDate fecha, Donante primerPuesto, Donante segundoPuesto, Donante tercerPuesto){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
    }

    public Integer getPosicion(Donante donante) {
        for (int i = 0; i < rankingCompletoOrdenado.size(); i++) {
            if (rankingCompletoOrdenado.get(i).getId().equals(donante.getId())) {
                return i + 1; // posición empieza en 1
            }
        }
        return null; // no está en el ranking
    }
    public List<Donante> getRankingCompleto() {
        return rankingCompletoOrdenado; }
}
