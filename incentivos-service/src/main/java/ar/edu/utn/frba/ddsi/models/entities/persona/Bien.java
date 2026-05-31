package ar.edu.utn.frba.ddsi.models.entities.persona;

import java.time.LocalDateTime;

public class Bien {
    private String nombre;
    private String descripcion;
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
}
