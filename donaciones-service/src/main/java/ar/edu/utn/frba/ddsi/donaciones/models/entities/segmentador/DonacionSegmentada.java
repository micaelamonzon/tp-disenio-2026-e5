package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;

import java.util.ArrayList;

public class DonacionSegmentada {
//    private PersonaDonante personaDonante;
    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();
    private Subcategoria subcategoria;

    public DonacionSegmentada(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }

}
