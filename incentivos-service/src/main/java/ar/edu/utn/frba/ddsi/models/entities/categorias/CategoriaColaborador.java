package ar.edu.utn.frba.ddsi.models.entities.categorias;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaColaborador implements CategoriaDeDonante{
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
    public void desbloquearMisiones(List<DonacionSinSegmentar> donaciones){
        for(int i = 0; i < misiones.size(); i++){
            if(misiones.get(i).seCompletoLaMision(donaciones) == Boolean.TRUE){
                misiones.get(i+1).setEstadoDeMision(EstadoDeMision.DESBLOQUEADA);
            }
        }
    }
    @Override
    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }

}
