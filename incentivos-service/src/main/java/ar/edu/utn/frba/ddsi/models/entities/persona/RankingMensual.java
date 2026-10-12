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

    @OneToMany(mappedBy = "ranking", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicion ASC")
    private List<RankingPosicion> posiciones = new ArrayList<>();

    public RankingMensual(LocalDate fecha, Donante primerPuesto, Donante segundoPuesto, Donante tercerPuesto,List<Donante> rankingOrdenado){
        this.fecha = fecha;
        this.primerPuesto = primerPuesto;
        this.segundoPuesto = segundoPuesto;
        this.tercerPuesto = tercerPuesto;
        for (int i = 0; i < rankingOrdenado.size(); i++) {
            this.posiciones.add(
                    new RankingPosicion(this, rankingOrdenado.get(i), i + 1)
            );
        }
    }

    public Integer getPosicion(Donante donante) {
        if (donante == null || this.posiciones == null) return null;
        return this.posiciones.stream()
                .filter(rp -> rp.getDonante() != null && rp.getDonante().getId().equals(donante.getId()))
                .map(RankingPosicion::getPosicion)
                .findFirst()
                .orElse(null);
    }
    public Integer getPosicionPorId(Long idDonante) {
        return posiciones.stream()
                .filter(rp -> rp.getDonante().getId().equals(idDonante))
                .map(RankingPosicion::getPosicion)
                .findFirst()
                .orElse(null);
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
