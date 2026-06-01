package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.persona.Tipo;
import lombok.Data;

@Data
public class Mision {
    String nombre;
    Tipo tipo;
    public Mision(String nombre){
        this.nombre = nombre;
    }
}
