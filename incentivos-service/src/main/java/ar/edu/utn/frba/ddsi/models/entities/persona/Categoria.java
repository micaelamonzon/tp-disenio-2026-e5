package ar.edu.utn.frba.ddsi.models.entities.persona;


import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Categoria") //categoria de donacion
public class Categoria extends Persistente {
    @Column
    private String nombre;

    public Categoria(String nombre){
        this.nombre = nombre;
    }


}
