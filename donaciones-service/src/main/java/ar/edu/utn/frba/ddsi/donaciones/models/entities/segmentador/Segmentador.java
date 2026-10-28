package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class Segmentador {


    public List<DonacionSegmentada> segmentar(DonacionSinSegmentar donacionSinSegmentar) {

        List<DonacionSegmentada> nuevasDonacionesSegmentadas = new ArrayList<>();

        Map<ClaveSegmentacion, List<Bien>> bienesAgrupados = donacionSinSegmentar.getBienes().stream()
                .collect(Collectors.groupingBy(
                        bien -> new ClaveSegmentacion(bien.getSubcategoria(), bien.getFechaDeVencimiento())
                ));

        bienesAgrupados.forEach((clave, listaBienes) -> {
            DonacionSegmentada donacionSegmentada = new DonacionSegmentada(clave.getSubcategoria());
            donacionSegmentada.setFechaDeIngreso(LocalDateTime.now());
            donacionSegmentada.setDonacionEntregada(false);
            donacionSegmentada.setEntidadBeneficiaria(null);
            listaBienes.forEach(donacionSegmentada::agregarBien);
            nuevasDonacionesSegmentadas.add(donacionSegmentada);
            donacionSinSegmentar.agregarDonacionSegmentada(donacionSegmentada);
        });

        return nuevasDonacionesSegmentadas;
    }
}