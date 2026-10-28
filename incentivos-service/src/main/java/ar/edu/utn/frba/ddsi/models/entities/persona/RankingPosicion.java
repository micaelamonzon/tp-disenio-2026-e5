package ar.edu.utn.frba.ddsi.models.entities.persona;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ranking_posicion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingPosicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_id")
    private RankingMensual ranking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id")
    private Donante donante;

    @Column(name = "posicion")
    private Integer posicion;

    public RankingPosicion(RankingMensual ranking, Donante donante, Integer posicion) {
        this.ranking = ranking;
        this.donante = donante;
        this.posicion = posicion;
    }
}