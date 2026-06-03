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

    public RankingMensual(LocalDate fecha, Donante primerPuesto, Donante segundoPuesto, Donante tercerPuesto,List<Donante> rankingCompletoOrdenado){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
        this.rankingCompletoOrdenado = rankingCompletoOrdenado;
    }

    public Integer getPosicion(Donante donante) {
        return (donante != null) ? getPosicionPorId(donante.getId()) : null;
    }

    public Integer getPosicionPorId(Long id) {
        if (id == null) return null;
        for (int i = 0; i < rankingCompletoOrdenado.size(); i++) {
            if (rankingCompletoOrdenado.get(i).getId().equals(id)) {
                return i + 1;
            }
        }
        return null;
    }
    public List<Donante> getRankingCompleto() {
        return rankingCompletoOrdenado; }
}
