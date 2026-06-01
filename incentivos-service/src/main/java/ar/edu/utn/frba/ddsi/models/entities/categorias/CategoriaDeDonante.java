package ar.edu.utn.frba.ddsi.models.entities.categorias;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;

import java.util.List;

public interface CategoriaDeDonante {

    public Boolean pasaSiguienteCategoria(List<DonacionSinSegmentar> donaciones);
    List<Mision> obtenerMisionesCompletadas(List<DonacionSinSegmentar> donaciones);
    public void agregarMision(Mision mision);
}
