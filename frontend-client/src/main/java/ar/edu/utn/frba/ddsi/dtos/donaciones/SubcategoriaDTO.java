package ar.edu.utn.frba.ddsi.dtos.donaciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubcategoriaDTO {
    private String nombre;
    private Boolean esPerecedero;
    private CategoriaDTO categoria;
}
