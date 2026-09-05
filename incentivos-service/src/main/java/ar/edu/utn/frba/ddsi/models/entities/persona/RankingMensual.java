package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ranking_mensual")
public class RankingMensual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha")
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_primer_puesto")
    private Donante primerPuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_segundo_puesto")
    private Donante segundoPuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tercer_puesto")
    private Donante tercerPuesto;

    @ElementCollection
    @CollectionTable(
            name = "ranking_posicion",
            joinColumns = @JoinColumn(name = "ranking_id")
    )
    @OrderColumn(name = "posicion")
    @Column(name = "donante_id")
    private List<Long> idsRankingCompleto = new ArrayList<>();

    public RankingMensual(LocalDate fecha, Donante primerPuesto, Donante segundoPuesto, Donante tercerPuesto,List<Donante> rankingCompletoOrdenado){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
        this.idsRankingCompleto  = rankingCompletoOrdenado.stream()
                .map(Donante::getId).toList();
    }

    public Integer getPosicion(Donante donante) {
        return (donante != null) ? getPosicionPorId(donante.getId()) : null;
    }

    public Integer getPosicionPorId(Long id) {
        if (id == null) return null;
        for (int i = 0; i < idsRankingCompleto.size(); i++) {
            if (idsRankingCompleto.get(i).equals(id)) {
                return i + 1;
            }
        }
        return null;
    }
    public List<Long> getRankingCompleto() {
        return idsRankingCompleto;
    }

    public String getNombrePrimerPuesto(){
        return getNombre(primerPuesto);
    }

    public String getNombreSegundoPuesto() {
        return getNombre(segundoPuesto);
    }

    public String getNombreTercerPuesto() {
        return getNombre(tercerPuesto);
    }

    private String getNombre(Donante donante) {
        if (donante == null) return "No existe ese Donante";
        return donante.getNombre() != null
                ? donante.getNombre() + " " + donante.getApellido()
                : donante.getRazonSocial();
    }
}
