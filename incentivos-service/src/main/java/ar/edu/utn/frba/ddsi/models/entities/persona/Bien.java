package ar.edu.utn.frba.ddsi.models.entities.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bien {
    private String nombre;
    private String descripcion;
    private Subcategoria subcategoria;
    private LocalDateTime fechaDeVencimiento;
    private EstadoDeUso esUsado;
    private Unidad tipoUnidad;
    private Integer cantidad;

    public Bien(String nombre, String descripcion, Subcategoria subcategoria, LocalDateTime fechaDeVencimiento, EstadoDeUso esUsado, Unidad tipoUnidad, Integer cantidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.subcategoria = subcategoria;
        this.fechaDeVencimiento = fechaDeVencimiento;
        this.esUsado = esUsado;
        this.tipoUnidad = tipoUnidad;
        this.cantidad = cantidad;
    }
}
