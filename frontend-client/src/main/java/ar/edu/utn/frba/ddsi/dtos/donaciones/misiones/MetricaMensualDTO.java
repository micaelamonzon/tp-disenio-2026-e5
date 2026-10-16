package ar.edu.utn.frba.ddsi.dtos.donaciones.misiones;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;


@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricaMensualDTO(
        String periodo,
        @JsonAlias("CantidadDonaciones") Integer cantidadDonaciones,
        @JsonAlias("OrganizacionesAyudadas") Integer organizacionesAyudadas
) {
    private static final Locale ES = Locale.forLanguageTag("es-AR");

    public String etiqueta() {
        try {
            YearMonth ym = YearMonth.parse(periodo);
            String mes = ym.getMonth().getDisplayName(TextStyle.SHORT, ES).replace(".", "");
            return Character.toUpperCase(mes.charAt(0)) + mes.substring(1) + " " + String.valueOf(ym.getYear()).substring(2);
        } catch (Exception e) {
            return periodo;
        }
    }

    public int cantidad() {
        return cantidadDonaciones != null ? cantidadDonaciones : 0;
    }
}
