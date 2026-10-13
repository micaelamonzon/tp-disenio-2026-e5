package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@Entity
@Table(name = "persona_juridica")
public class PersonaJuridica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuit")
    private String cuit;

    @Column(name = "razon_social")
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private Tipo tipo;

    @Column(name = "rubro")
    private String rubro;

    @Column(name = "fecha_de_registro")
    private LocalDateTime fechaDeRegistro;

    @OneToMany (cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_juridica_id", referencedColumnName = "id")
    private List<Representante> personasRepresentantes = new ArrayList<>();

    @OneToMany (cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_juridica_id", referencedColumnName = "id")
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "medio_predeterminado_id")
    private MedioDeNotificacion medioDeNotificacionPredeterminado;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "donanteJuridico",cascade = CascadeType.ALL)
    private List<DonacionSinSegmentar> donaciones = new ArrayList<>();




    public PersonaJuridica(String cuit,String razonSocial,Tipo tipo, String rubro) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro= rubro;
    }

    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }


    public void agregarDonacion(DonacionSinSegmentar donacion){
        this.donaciones.add(donacion);
    }
}
