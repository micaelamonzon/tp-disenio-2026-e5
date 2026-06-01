package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Data
public class Bien {
    private String nombre;
    private String descripcion;
    private Foto foto;
    private Subcategoria subcategoria;
    private LocalDateTime fechaDeVencimiento;
    private EstadoDeUso esUsado;
    private Unidad tipoUnidad;
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
