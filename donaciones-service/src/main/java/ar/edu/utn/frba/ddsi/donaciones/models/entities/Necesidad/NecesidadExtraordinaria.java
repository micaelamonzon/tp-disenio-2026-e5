package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Getter
@NoArgsConstructor
@Entity
@Table(name = "necesidad_extraordinaria")
public class NecesidadExtraordinaria extends Necesidad {
    @Column(name = "cantidad_requerida")
    private int cantidadRequerida;

    @Column(name = "cantidad_recibida")
    private int cantidadRecibida;

    public NecesidadExtraordinaria(Subcategoria subcategoria, String descripcion, int cantidadRequerida) {
        super(subcategoria, descripcion);
        this.cantidadRequerida = cantidadRequerida;
        this.cantidadRecibida = 0;
    }

    @Override
    public void satisfacer(int cantidad) {
        this.cantidadRecibida += cantidad;
        if (this.cantidadRecibida >= this.cantidadRequerida) {
            marcarComoSatisfecha();
        }
    }

    @Override public String tipo() { return "EXTRAORDINARIA"; }
    @Override public int cantidadSolicitada() { return cantidadRequerida; }
    @Override public int cantidadRecibida() { return cantidadRecibida; }
}
