package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.EntidadBeneficiaria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.*;
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
@Table(name = "donacion_segmentada")
public class DonacionSegmentada  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="donanteHumano_id", referencedColumnName = "id")
   private PersonaHumana donanteHumano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="donanteJuridico_id", referencedColumnName = "id")
    private PersonaJuridica donanteJuridico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_donante")
    private TipoDonante tipoDonante;

    @OneToMany
    @JoinColumn(name = "donacion_segmentada_id", referencedColumnName = "id")
    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "subcategoria_id")
    private Subcategoria subcategoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual")
    private EstadoDonacion estadoActual;

    @OneToMany(mappedBy = "donacionSegmentada")
    private List<CambioEstado> historial = new ArrayList<>();

    @Column(name = "fecha_de_ingreso")
    private LocalDateTime fechaDeIngreso;

    @Column(name = "donacion_entregada")
    private Boolean donacionEntregada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="entidadBeneficiaria_id", referencedColumnName = "id")
    private EntidadBeneficiaria entidadBeneficiaria;

    @Column(name = "fecha_de_entrega")
    private LocalDate fechaEntrega;

    @ManyToOne
    @JoinColumn(name = "necesidad_id")
    private Necesidad necesidad;

    public DonacionSegmentada(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
        this.estadoActual = EstadoDonacion.EN_DEPOSITO;
        historial.add(new CambioEstado(null, EstadoDonacion.EN_DEPOSITO,
                "Donación registrada en depósito", "SISTEMA"));
    }



    public void agregarBien(Bien bien) {
        this.bienesDelMismoTipo.add(bien);
    }

    public void cambiarEstado(EstadoDonacion nuevo, String justificacion, String responsableId) {
        this.estadoActual.validarTransicion(nuevo);
        historial.add(new CambioEstado(this.estadoActual, nuevo, justificacion, responsableId));
        this.estadoActual = nuevo;
    }


}