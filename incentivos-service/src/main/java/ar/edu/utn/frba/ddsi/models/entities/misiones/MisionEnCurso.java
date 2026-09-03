package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeMision;
import lombok.Data;

@Data
public class MisionEnCurso {
    Mision misionActual;
    CategoriaDeMision categoriaActual;

    public MisionEnCurso(Mision misionActual, CategoriaDeMision categoriaActual) {
        this.misionActual = misionActual;
        this.categoriaActual = misionActual.get;
    }

}


