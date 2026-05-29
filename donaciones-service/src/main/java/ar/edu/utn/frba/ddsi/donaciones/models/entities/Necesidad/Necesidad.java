package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public abstract class Necesidad {
    private Subcategoria subcategoria;
    private String descripcion;
    private boolean estaSatisfecha;

    public Necesidad(Subcategoria subcategoria, String descripcion) {
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
        this.estaSatisfecha = false;
    }

    public abstract void satisfacer(int cantidadRecibida);

    protected void marcarComoSatisfecha() {
        this.estaSatisfecha = true;
    }
}