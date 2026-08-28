package ar.edu.utn.frba.ddsi.models.entities.categorias;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoriaEnCurso {
    TipoCategoria tipo; //tipo de categoria
    ArrayList<Mision> misiones; //lista de misiones consecutivas a cumplir -> se llena en el servis de incentivos
    TipoCategoria categoriaSiguiente;

    public CategoriaEnCurso(TipoCategoria tipo, TipoCategoria categoriaSiguiente) {
        this.tipo = tipo;
        this.misiones = new ArrayList<>();
        this.categoriaSiguiente = categoriaSiguiente;
    }

    public void agregarMisiones(Mision mision){
        this.misiones.add(mision);
    }

    public Boolean pasaSiguienteCategoria(List<DonacionSinSegmentar> donaciones){
        if(this.categoriaSiguiente != null){
            return misiones.stream().allMatch( m -> m.seCompletoLaMision(donaciones) == Boolean.TRUE);
        }
        return false;
    }

    public ArrayList<Mision> obtenerMisionesCompletadas(List<DonacionSinSegmentar> donaciones){
        return this.misiones.stream().filter( m -> m.seCompletoLaMision(donaciones) == Boolean.TRUE).collect(Collectors.toCollection(ArrayList::new));
    }
    public void desbloquearMisiones(List<DonacionSinSegmentar> donaciones){
        for(int i = 0; i < misiones.size(); i++){
            if(misiones.get(i).seCompletoLaMision(donaciones) == Boolean.TRUE && i+1 < misiones.size()){
                misiones.get(i+1).setEstadoDeMision(EstadoDeMision.DESBLOQUEADA);
            }
        }
    }
}
