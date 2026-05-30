package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import lombok.Data;

import java.util.List;

@Data
public class Completitud implements Mision{

    private List<Categoria> categorias;
    private Integer distanciaDelObjetivo;
    private Integer progreso;
    private EstadoDeMision estado;

    public Completitud(List<Categoria> categorias){
        this.categorias = categorias;
    }
    @Override
    public Boolean seCompletoLaMision() {
        //realizar donaciones de X categorías distintas.
        return progreso == 100;
    }


}
