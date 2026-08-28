package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaEnCurso;
import ar.edu.utn.frba.ddsi.models.entities.categorias.TipoCategoria;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import lombok.Data;

import java.util.List;

@Data
public class MisionEnCurso {
    Mision misionActual;
    CategoriaEnCurso categoriaActual;

    public MisionEnCurso( Mision misionActual, CategoriaEnCurso categoriaActual) {
        this.misionActual = misionActual;
        this.categoriaActual = categoriaActual;
    }

}


