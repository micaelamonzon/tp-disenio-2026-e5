package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Data

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bien")
public class Bien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToOne
    @JoinColumn(name = "id_foto", referencedColumnName = "id")
    private Foto foto;

    @ManyToOne
    @JoinColumn(name = "id_subcategoria", referencedColumnName = "id")
    private Subcategoria subcategoria;

    @Column(name = "fechaDeVencimiento")
    private LocalDateTime fechaDeVencimiento;

    private EstadoDeUso esUsado;
    private Unidad tipoUnidad;

    @Column(name = "Cantidad")
    private Integer cantidad;
    public Bien(String nombre, String descripcion, Foto foto, Subcategoria subcategoria, LocalDateTime fechaDeVencimiento, EstadoDeUso esUsado, Unidad tipoUnidad, Integer cantidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto = foto;
        this.subcategoria = subcategoria;
        this.fechaDeVencimiento = fechaDeVencimiento;
        this.esUsado = esUsado;
        this.tipoUnidad = tipoUnidad;
        this.cantidad = cantidad;
    }
    public boolean sePuedeDonar(){
        if(subcategoria.getEsPerecedero()){
            return this.getFechaDeVencimiento().isAfter(LocalDateTime.now());
        }
        if(this.getEsUsado() != null){
            return this.getEsUsado() == EstadoDeUso.NUEVO;
        }
        return true;
    }
}
