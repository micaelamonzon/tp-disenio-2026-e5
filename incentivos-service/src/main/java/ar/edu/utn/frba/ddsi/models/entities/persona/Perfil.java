package ar.edu.utn.frba.ddsi.models.entities.persona;
import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Perfil")
public class Perfil extends Persistente {

    @ElementCollection
    @CollectionTable(name = "perfil_insignia", joinColumns = @JoinColumn(name="perfil_id"))
    @Column(name = "insignia")
    private List<String> insignias;

    @Column
    private String nombreDeUsuario; // lo sacamos del nombre y apellido del donante o de la razón social de la empresa

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName ="id")
    private CategoriaDeMision categoria;

    @Column
    private Integer totalHistoricoPorPeriodo;

    @Transient
    private List<MetricaMensual> evolucionMensual;

    @Column
    private Boolean insigniasPrivadas;


    public Perfil (String nombreDeUsuario, CategoriaDeMision categoria) {
        this.insignias = new ArrayList<>();
        this.insigniasPrivadas = false;
        this.nombreDeUsuario = nombreDeUsuario;
        this.categoria = categoria;
        this.evolucionMensual = new ArrayList<>();
    }

    public void agregarInsignia(String insignia){
        this.insignias.add(insignia);
    }
}
