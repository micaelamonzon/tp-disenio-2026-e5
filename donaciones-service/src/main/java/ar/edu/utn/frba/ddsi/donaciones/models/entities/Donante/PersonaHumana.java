package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Persistent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "persona_humana")
public class PersonaHumana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "fecha_de_nacimiento")
    private LocalDate fechaDeNaciemiento;

    @Column(name = "fecha_de_registro")
    private LocalDateTime fechaDeRegistro;

    // Última vez que se le avisó por inactividad. Evita repetir el aviso todos los días:
    // solo se vuelve a avisar si hubo actividad (una donación) después de este aviso.
    @Column(name = "fecha_ultimo_aviso_inactividad")
    private LocalDateTime fechaUltimoAvisoInactividad;

    @Column(name = "dni")
    private Integer DNI;

    @Column(name = "genero")
    private String genero;

    @Column(name = "direccion")
    private String direccion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "persona_humana_id", referencedColumnName = "id")
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "medio_predeterminado_id")
    private MedioDeNotificacion medioDeNotificacionPredeterminado;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "donanteHumano", cascade = CascadeType.ALL)
    private List<DonacionSinSegmentar> donaciones = new ArrayList<>(); //al acceder a esta donacion sabremos que donaciones segmentadas salieron


    public PersonaHumana(String nombre,String apellido,Integer numeroDeDocumento,String genero,String direccion, LocalDate fechaDeNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.DNI = numeroDeDocumento;
        this.genero = genero;
        this.direccion = direccion;
        this.fechaDeNaciemiento = fechaDeNacimiento;
        this.fechaDeRegistro = LocalDateTime.now();

    }

    public void calcularEdad() {
        if(this.fechaDeNaciemiento == null){
            this.edad = null;
            return ;
        }
        LocalDateTime fechaActual = LocalDateTime.now();
        this.edad = fechaActual.getYear() - this.fechaDeNaciemiento.getYear();

    }


    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }


    public void agregarDonacion(DonacionSinSegmentar donacion){
        this.donaciones.add(donacion);
    }

}