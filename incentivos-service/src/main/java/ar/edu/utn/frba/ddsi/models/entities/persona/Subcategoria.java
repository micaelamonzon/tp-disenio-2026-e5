package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.Data;

@Data
public class Subcategoria {
    private String nombre;
    private boolean esPerecedero;
    private Categoria categoria;

    public Subcategoria(String nombre, Boolean esPerecedero, Categoria categoria){
        this.nombre = nombre;
        this.esPerecedero = esPerecedero;
        this.categoria = categoria;

    }
}
