package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaMision;
import lombok.Data;

@Data
public class MisionEnCurso {
    Mision misionActual;
    CategoriaMision categoriaActual;

    public MisionEnCurso( Mision misionActual, CategoriaMision categoriaActual) {
        this.misionActual = misionActual;
        this.categoriaActual = categoriaActual;
    }

}


