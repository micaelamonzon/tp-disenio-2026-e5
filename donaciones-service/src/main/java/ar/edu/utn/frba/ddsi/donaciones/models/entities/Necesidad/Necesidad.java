package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "necesidad")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Necesidad{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entidad_beneficiaria_id")
    private EntidadBeneficiaria entidadBeneficiaria; 

    @ManyToOne
    @JoinColumn(name = "subcategoria_id")
    private Subcategoria subcategoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "esta_satisfecho")
    private boolean estaSatisfecha;

    @OneToMany(mappedBy = "necesidad")
    private List<DonacionSegmentada> donacionesRecibidas = new ArrayList<>();

    public Necesidad(Subcategoria subcategoria, String descripcion) {
        this(null, subcategoria, descripcion);
    }

    public Necesidad(EntidadBeneficiaria entidadBeneficiaria, Subcategoria subcategoria, String descripcion) {
        this.entidadBeneficiaria = entidadBeneficiaria;
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.estaSatisfecha = false;
    }

    public abstract void satisfacer(int cantidadRecibida);

    protected void marcarComoSatisfecha() {
        this.estaSatisfecha = true;
    }

    public int contarDonacionesDesde(LocalDateTime fechaLimite) {
        return (int) this.donacionesRecibidas.stream()
                .filter(d -> d.getFechaEntrega() != null && d.getFechaEntrega().isAfter(fechaLimite))
                .count();
    }

    public boolean estaSatisfecha() {
        return this.estaSatisfecha;
    }


}
