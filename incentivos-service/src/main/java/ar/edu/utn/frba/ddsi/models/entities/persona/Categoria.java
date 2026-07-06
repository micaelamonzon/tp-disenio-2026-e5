package ar.edu.utn.frba.ddsi.models.entities.persona;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Categoria {
    private String nombre;

    public Categoria(String nombre){
        this.nombre = nombre;
    }


}
