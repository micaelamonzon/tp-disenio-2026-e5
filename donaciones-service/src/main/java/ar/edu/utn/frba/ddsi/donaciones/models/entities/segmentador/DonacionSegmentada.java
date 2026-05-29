package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
@Data
public class DonacionSegmentada{

    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();
    private Subcategoria subcategoria;

    public void agregarBien(Bien bien){
        this.bienesDelMismoTipo.add(bien);
    }

}
