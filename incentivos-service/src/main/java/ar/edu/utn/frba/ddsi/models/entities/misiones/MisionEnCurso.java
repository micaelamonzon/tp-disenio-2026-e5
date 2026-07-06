package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MisionEnCurso {
    Mision misionActual;
    Categoria categoriaActual;

    public MisionEnCurso( Mision misionActual, Categoria categoriaActual) {
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
        if (categoriaActual == Categoria.COLABORADOR) {
            categoriaActual = Categoria.SOSTENEDOR;
        } else if (categoriaActual == Categoria.SOSTENEDOR) {
            categoriaActual = Categoria.TRANSFORMADOR;
        } else if (categoriaActual == Categoria.TRANSFORMADOR) {
            categoriaActual = Categoria.TRANSFORMADOR;

        }
    }
}


