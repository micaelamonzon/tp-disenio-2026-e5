package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;

import java.util.ArrayList;
@Data
public class DonacionSegmentada{

    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();
    private Subcategoria subcategoria;
    private EstadoDonacion_old estado = EstadoDonacion_old.EN_DEPOSITO;

    public void agregarBien(Bien bien){
        this.bienesDelMismoTipo.add(bien);
    }

}
