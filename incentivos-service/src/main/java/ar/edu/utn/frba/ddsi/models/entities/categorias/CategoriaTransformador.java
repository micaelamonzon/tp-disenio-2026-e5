package ar.edu.utn.frba.ddsi.models.entities.categorias;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;

import java.util.List;

public class CategoriaTransformador implements CategoriaDeDonante{
    List<Mision> misiones;
    CategoriaDeDonante categoriaSiguiente;

    @Override
    public Boolean cumplioTodasLasMisiones(){
        return misiones.stream().allMatch( m -> m.seCompletoLaMision() == Boolean.TRUE);
    }
    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }
}
