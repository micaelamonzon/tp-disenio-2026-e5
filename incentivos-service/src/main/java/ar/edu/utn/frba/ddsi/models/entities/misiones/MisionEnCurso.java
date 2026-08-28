package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.TipoCategoria;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import lombok.Data;

import java.util.List;

@Data
public class MisionEnCurso {
    Mision misionActual;
    TipoCategoria categoriaActual;

    public MisionEnCurso( Mision misionActual, TipoCategoria categoriaActual) {
        this.misionActual = misionActual;
        this.categoriaActual = categoriaActual;
    }

    public boolean pasaSiguienteCategoria(List<DonacionSinSegmentar> donaciones){

        if(misionActual.seCompletoLaMision(donaciones)){
            this.avanzarCategoria();
            return true;
        }

        return false;
    }

    public void  avanzarCategoria() {
        if (categoriaActual == TipoCategoria.COLABORADOR) {
            categoriaActual = TipoCategoria.SOSTENEDOR;
        } else if (categoriaActual == TipoCategoria.SOSTENEDOR) {
            categoriaActual = TipoCategoria.TRANSFORMADOR;
        } else if (categoriaActual == TipoCategoria.TRANSFORMADOR) {
            categoriaActual = TipoCategoria.TRANSFORMADOR;

        }
    }
}


