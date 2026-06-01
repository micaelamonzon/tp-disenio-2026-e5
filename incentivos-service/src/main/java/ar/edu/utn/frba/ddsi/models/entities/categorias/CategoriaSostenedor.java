package ar.edu.utn.frba.ddsi.models.entities.categorias;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaSostenedor implements CategoriaDeDonante{
    List<Mision> misiones; //lista de misiones consecutivas a cumplir
    CategoriaDeDonante categoriaSiguiente;

    @Override
    public Boolean pasaSiguienteCategoria(List<DonacionSinSegmentar> donaciones){
        return misiones.stream().allMatch( m -> m.seCompletoLaMision(donaciones) == Boolean.TRUE);
    }
    @Override
    public List<Mision> obtenerMisionesCompletadas(List<DonacionSinSegmentar> donaciones){
        return this.misiones.stream().filter( m -> m.seCompletoLaMision(donaciones) ).collect(Collectors.toList());
    }
    @Override
    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }
}
