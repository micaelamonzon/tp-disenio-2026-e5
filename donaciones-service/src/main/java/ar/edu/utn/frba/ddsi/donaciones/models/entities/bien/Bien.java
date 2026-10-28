package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bien")
public class Bien extends Persistente {
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_subcategoria", referencedColumnName = "id")
    private Subcategoria subcategoria;

    @Column(name = "fechaDeVencimiento")
    private LocalDateTime fechaDeVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "es_usado")
    private EstadoDeUso esUsado;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad")
    private Unidad tipoUnidad;

    @Column(name = "Cantidad")
    private Integer cantidad;

    @Column(name = "foto_path")
    private String fotoPath;

    public Bien(String nombre, String descripcion, Subcategoria subcategoria, LocalDateTime fechaDeVencimiento, EstadoDeUso esUsado, Unidad tipoUnidad, Integer cantidad, String fotoPath) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategoria = subcategoria;
        this.fechaDeVencimiento = fechaDeVencimiento;
        this.esUsado = esUsado;
        this.tipoUnidad = tipoUnidad;
        this.cantidad = cantidad;
        this.fotoPath = fotoPath;
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
