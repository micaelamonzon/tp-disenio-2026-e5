package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import lombok.Data;

import java.util.List;

@Data
public class Completitud implements Tipo {

    private List<Categoria> categorias;
    private Integer distanciaDelObjetivo = 100;
    private Integer progreso;


    public Completitud(List<Categoria> categorias){
        this.categorias = categorias;
    }

    //Completitud: realizar donaciones de X categorías distintas.
    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {

        List<Categoria> todasLasCategorias = donaciones.stream()
                .flatMap(d -> d.getBienes().stream())
                .map(bien -> bien.getSubcategoria().getCategoria())
                .toList();

        long categoriasUnicas = todasLasCategorias.stream()
                .distinct()
                .count();

        boolean todasSonDistintas = todasLasCategorias.size() == categoriasUnicas;


        if (todasSonDistintas) {
            this.subirProgreso(donaciones.size());
            this.distanciaDelObjetivo -= this.progreso;
            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }

    public void subirProgreso(Integer cantDonaciones){
        this.progreso += (100/cantDonaciones);
    }


}
