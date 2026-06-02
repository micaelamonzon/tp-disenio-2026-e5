package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Donacion;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@NoArgsConstructor
public abstract class Necesidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private EntidadBeneficiaria entidadBeneficiaria;

    @Transient
    private Subcategoria subcategoria;

    private String descripcion;
    private boolean estaSatisfecha;

    @ManyToMany
    private List<Donacion> donacionesRecibidas = new ArrayList<>();

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

    public int contarDonacionesDesde(LocalDate fechaLimite) {
        return (int) this.donacionesRecibidas.stream()
                .filter(d -> d.getFechaEntrega() != null && d.getFechaEntrega().isAfter(fechaLimite))
                .count();
    }

}
