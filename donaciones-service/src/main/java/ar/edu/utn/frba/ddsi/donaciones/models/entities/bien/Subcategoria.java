package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

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
public class Subcategoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
