package ar.edu.utn.frba.ddsi.models.entities.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
