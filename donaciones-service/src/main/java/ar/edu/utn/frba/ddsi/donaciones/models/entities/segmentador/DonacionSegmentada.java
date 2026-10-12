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
import jakarta.persistence.CascadeType;

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

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "donacion_segmentada_id", referencedColumnName = "id")
    private List<Bien> bienesDelMismoTipo = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "subcategoria_id")
    private Subcategoria subcategoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual")
    private EstadoDonacion estadoActual;

    @OneToMany(mappedBy = "donacionSegmentada", cascade = CascadeType.ALL)
    private List<CambioEstado> historial = new ArrayList<>();

    @Column(name = "fecha_de_ingreso")
    private LocalDateTime fechaDeIngreso;

    @Column(name = "donacion_entregada")
    private Boolean donacionEntregada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="entidadBeneficiaria_id", referencedColumnName = "id")
    private EntidadBeneficiaria entidadBeneficiaria;

    @Column(name = "fecha_de_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "fecha_de_asignacion")
    private LocalDateTime fechaDeAsignacion;

    @Column
    private String fotoPath;

    @ManyToOne
    @JoinColumn(name = "necesidad_id")
    private Necesidad necesidad;

    private void registrarCambio(EstadoDonacion anterior, EstadoDonacion nuevo,
                                 String justificacion, String responsableId) {
        CambioEstado cambio = new CambioEstado(anterior, nuevo, justificacion, responsableId);
        cambio.setDonacionSegmentada(this);
        historial.add(cambio);
    }
    public DonacionSegmentada(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
        this.estadoActual = EstadoDonacion.EN_DEPOSITO;
        registrarCambio(null, EstadoDonacion.EN_DEPOSITO,"Donación registrada en depósito", "SISTEMA");
    }

    public String calcularDescripcion(){
        String cant = this.calcularCantidadDeBienes().toString();

        return cant + " unidades de " + this.getSubcategoria().getNombre();
    }

    public void agregarBien(Bien bien) {
        this.bienesDelMismoTipo.add(bien);
    }

    public void cambiarEstado(EstadoDonacion nuevo, String justificacion, String responsableId) {
        this.estadoActual.validarTransicion(nuevo);
        registrarCambio(this.estadoActual, nuevo, justificacion, responsableId);
        this.estadoActual = nuevo;
    }

    public Integer calcularCantidadDeBienes() {
        return bienesDelMismoTipo.stream().mapToInt(Bien::getCantidad).sum();
    }

    public Double calcularVolumen(){
        Integer cantBienes = this.getBienesDelMismoTipo().size();
        return cantBienes.doubleValue() * 2;
    }


    public Double calcularPeso(){
        Integer cantBienes = this.getBienesDelMismoTipo().size();
        return cantBienes.doubleValue() * 3;
    }



}