package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria extends Persistente {
    @Column(name = "nombre")
    private String nombre;

    public Categoria(String nombre){
        this.nombre = nombre;
    }
}
