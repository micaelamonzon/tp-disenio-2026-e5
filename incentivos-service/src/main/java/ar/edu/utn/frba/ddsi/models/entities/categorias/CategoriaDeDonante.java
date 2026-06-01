package ar.edu.utn.frba.ddsi.models.entities.categorias;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaDeDonante {
    List<Mision> misiones; //lista de misiones consecutivas a cumplir

    NombreDeCategoria nombreDeCategoriaActual;
    CategoriaDeDonante categoriaSiguiente; // si esta en la ultima categoria, categoriaSiguiente es null

    public CategoriaDeDonante(NombreDeCategoria nombreDeCategoriaActual) {
        this.nombreDeCategoriaActual = nombreDeCategoriaActual;

    }
    public void establecerCategoriaSiguiente(){
        NombreDeCategoria nombreDeCatSig = this.nombreDeCategoriaActual.obtenerSiguiente();
            if(nombreDeCatSig != null) {
                this.categoriaSiguiente = new CategoriaDeDonante(nombreDeCatSig);
            }
            this.categoriaSiguiente = null;
    }

    public Boolean pasaSiguienteCategoria(List<DonacionSinSegmentar> donaciones){
        if(this.categoriaSiguiente != null){
            return misiones.stream().allMatch( m -> m.seCompletoLaMision(donaciones) == Boolean.TRUE);
        }
        return false;
    }

    public List<Mision> obtenerMisionesCompletadas(List<DonacionSinSegmentar> donaciones){
        return this.misiones.stream().filter( m -> m.seCompletoLaMision(donaciones) == Boolean.TRUE).collect(Collectors.toList());
    }
    public void desbloquearMisiones(List<DonacionSinSegmentar> donaciones){
        for(int i = 0; i < misiones.size(); i++){
            if(misiones.get(i).seCompletoLaMision(donaciones) == Boolean.TRUE && i+1 < misiones.size()){
                misiones.get(i+1).setEstadoDeMision(EstadoDeMision.DESBLOQUEADA);
            }
        }
        if(misiones.get(misiones.size()-1).seCompletoLaMision(donaciones) == Boolean.TRUE){
            misiones.get(misiones.size()-1).setEstadoDeMision(EstadoDeMision.DESBLOQUEADA);
        }
    }

    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }

}
