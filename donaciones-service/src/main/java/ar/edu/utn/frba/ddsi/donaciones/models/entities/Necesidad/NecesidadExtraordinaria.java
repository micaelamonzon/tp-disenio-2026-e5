package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Getter
@NoArgsConstructor
public class NecesidadExtraordinaria extends Necesidad {
    private int cantidadRequerida;
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
}
