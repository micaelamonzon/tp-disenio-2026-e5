package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Segmentador {

    public ArrayList<DonacionSegmentada> segmentar(DonacionSinSegmentar donacionSinSegmentar){

        ArrayList<DonacionSegmentada> nuevasDonacionesSegmentadas = new ArrayList<>();
        Map<Subcategoria, List<Bien>> bienesAgrupados = donacionSinSegmentar.getBienes().stream().collect(Collectors.groupingBy(Bien::getSubcategoria));

        bienesAgrupados.forEach((subcategoria, listaBienes) -> {

            DonacionSegmentada donacionSegmentada = new DonacionSegmentada(subcategoria);
            donacionSegmentada.setBienesDelMismoTipo(new ArrayList<>(listaBienes));

            nuevasDonacionesSegmentadas.add(donacionSegmentada);
        });

        return nuevasDonacionesSegmentadas;
    }
}
