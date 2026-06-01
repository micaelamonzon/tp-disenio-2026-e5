package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.YearMonth;
@Data
@AllArgsConstructor
public class MetricaMensual {
    private YearMonth periodo;
    private Integer CantidadDonaciones;
    private Integer OrganizacionesAyudadas;
}
