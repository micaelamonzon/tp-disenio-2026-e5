package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;
import org.hibernate.boot.models.annotations.internal.SubselectAnnotation;

import java.time.LocalDateTime;

@Data
public class ClaveSegmentacion {
    private Subcategoria subcategoria;
    private LocalDateTime fechaVencimiento;

    public ClaveSegmentacion(Subcategoria subcategoria, LocalDateTime fechaDeVencimiento) {
        this.subcategoria = subcategoria;
        this.fechaVencimiento = fechaDeVencimiento;
    }
}
