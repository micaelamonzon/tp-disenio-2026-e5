package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Representante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "entidad_beneficiaria")
public class EntidadBeneficiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    // Los representantes pertenecen a la entidad (composición): se guardan y se borran con ella
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entidad_beneficiaria_id", referencedColumnName = "id")
    private List<Representante> representantes;

    @OneToMany(mappedBy = "entidadBeneficiaria")
    private List<Necesidad> necesidades;


    public EntidadBeneficiaria() {
        this.necesidades = new ArrayList<>();
        this.representantes = new ArrayList<>();
    }

    public void registrarNecesidadExtraordinaria(Subcategoria subcategoria,
                                                 String descripcion,
                                                 int cantidadRequerida) {
        this.necesidades.add(
                new NecesidadExtraordinaria(subcategoria, descripcion, cantidadRequerida)
        );
    }

    public void registrarNecesidadRecurrente(Subcategoria subcategoria,
                                             String descripcion,
                                             int cantidadObjetivo,
                                             TipoPeriodo periodo) {
        this.necesidades.add(
                new NecesidadRecurrente(subcategoria, descripcion, cantidadObjetivo, periodo)
        );
    }

    public List<Necesidad> obtenerNecesidadesPendientes() {
        return necesidades.stream()
                .filter(n -> !n.isEstaSatisfecha())
                .toList();


    }

    public int obtenerCantidadDonacionesUltimoTrimestre() {
        LocalDate haceTresMeses = LocalDate.now().minusMonths(3);

        return this.necesidades.stream()
                .mapToInt(n -> n.contarDonacionesDesde(haceTresMeses))
                .sum();
    }

}
