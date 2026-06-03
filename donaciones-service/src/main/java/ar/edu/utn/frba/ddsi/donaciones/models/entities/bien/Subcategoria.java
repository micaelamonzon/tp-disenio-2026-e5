package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Subcategoria {
    private String nombre;
    private Boolean esPerecedero;
    private Categoria categoria;

    public Subcategoria(String nombre, Boolean esPerecedero, Categoria categoria){
        this.nombre = nombre;
        this.esPerecedero = esPerecedero;
        this.categoria = categoria;

    }




}
