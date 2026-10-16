package ar.edu.utn.frba.ddsi.dtos.donaciones.misiones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

// Lo que devuelve incentivos en GET /{idDonante}/metricas: el impacto del donante para su perfil
@JsonIgnoreProperties(ignoreUnknown = true)
public record MetricasImpactoDTO(
        Long idDonante,
        String nombreDonante,
        Integer totalDonacionesHistoricas,   // todas las realizadas, en cualquier estado
        Integer totalDonacionesEntregadas,   // las que ya llegaron a una entidad
        String mesConMasDonaciones,
        Long cantidadDonacionesEnMesPico,
        List<MetricaMensualDTO> evolucionMensual,
        Integer comparacionConMesAnterior,
        Integer totalOrganizacionesAyudadas,
        Integer posicionEnRanking
) {
    private static final Locale ES = Locale.forLanguageTag("es-AR");

    public List<MetricaMensualDTO> evolucion() {
        return evolucionMensual != null ? evolucionMensual : List.of();
    }

    // Valor más alto del gráfico, para calcular el alto de cada barra
    public int maximoMensual() {
        return evolucion().stream().mapToInt(MetricaMensualDTO::cantidad).max().orElse(0);
    }

    public String mesPicoLegible() {
        if (mesConMasDonaciones == null) return null;
        try {
            YearMonth ym = YearMonth.parse(mesConMasDonaciones);
            return ym.getMonth().getDisplayName(TextStyle.FULL, ES) + " de " + ym.getYear();
        } catch (Exception e) {
            return mesConMasDonaciones;
        }
    }

    public String mesPicoNombre() {
        if (mesConMasDonaciones == null) return null;
        try {
            String mes = YearMonth.parse(mesConMasDonaciones).getMonth().getDisplayName(TextStyle.FULL, ES);
            return Character.toUpperCase(mes.charAt(0)) + mes.substring(1);
        } catch (Exception e) {
            return mesConMasDonaciones;
        }
    }

    public int diferenciaMensual() {
        return comparacionConMesAnterior != null ? comparacionConMesAnterior : 0;
    }
}
