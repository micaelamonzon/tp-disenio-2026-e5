package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subcategoria")
public class Subcategoria extends Persistente {
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "esPerecedero")
    private Boolean esPerecedero;

    @ManyToOne
    @JoinColumn(name = "id_Categoria", referencedColumnName = "id")
    private Categoria categoria;

    public Subcategoria(String nombre, Boolean esPerecedero, Categoria categoria){
        this.nombre = nombre;
        this.esPerecedero = esPerecedero;
        this.categoria = categoria;

    }

}
