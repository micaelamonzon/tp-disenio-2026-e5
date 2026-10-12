package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Representante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
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

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    // Los representantes pertenecen a la entidad (composición), asi quee se guardan y se borran con ella
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entidad_beneficiaria_id", referencedColumnName = "id")
    private List<Representante> representantes;

    @OneToMany(mappedBy = "entidadBeneficiaria", cascade = CascadeType.ALL)
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
        LocalDateTime haceTresMeses = LocalDateTime.now().minusMonths(3);

        return this.necesidades.stream()
                .mapToInt(n -> n.contarDonacionesDesde(haceTresMeses))
                .sum();
    }

}
