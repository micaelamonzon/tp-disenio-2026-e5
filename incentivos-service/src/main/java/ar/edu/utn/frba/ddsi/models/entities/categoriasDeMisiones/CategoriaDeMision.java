package ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "CategoriaDeMision")
public class CategoriaDeMision extends Persistente {
    @Enumerated(EnumType.STRING)
    private TipoCategoria nombre; //tipo o nombre de categoria

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    @OrderBy("ordenSecuencial ASC")
    private List<Mision> misiones;

    @Enumerated(EnumType.STRING)
    private TipoCategoria categoriaSiguiente;

    public CategoriaDeMision(TipoCategoria nombre, TipoCategoria categoriaSiguiente) {
        this.nombre = nombre;
        this.misiones = new ArrayList<>();
        this.categoriaSiguiente = categoriaSiguiente;
    }

    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }

}
