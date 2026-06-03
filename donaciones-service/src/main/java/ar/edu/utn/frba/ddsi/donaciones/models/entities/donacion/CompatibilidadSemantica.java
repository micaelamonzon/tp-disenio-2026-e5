package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.NecesidadExtraordinaria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.NecesidadRecurrente;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

@Setter
@Component
public class CompatibilidadSemantica implements Strategy_AlgoritmosMatchmaking {
    private double a1; // Peso de Similitud Textual
    private double a2; // Peso de Cobertura de Volumen
    private double a3; // Peso de Tipo de Necesidad

    public CompatibilidadSemantica(
            @Value("${match.a1}") double a1,
            @Value("${match.a2}") double a2,
            @Value("${match.a3}") double a3) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }

    @Override
    public double calcularPuntaje(Donacion donacion, Necesidad necesidad) {
        double x1 = calcularSimilitudTextual(donacion, necesidad);
        double x2 = calcularCoberturaVolumen(donacion, necesidad);
        double x3 = calcularTipoNecesidad(necesidad);

        return (this.a1 * x1) + (this.a2 * x2) + (this.a3 * x3);
    }

    private double calcularSimilitudTextual(Donacion donacion, Necesidad necesidad) {
        String nombreBien = donacion.getBien().getNombre();
        String descNecesidad = necesidad.getDescripcion();

        if (nombreBien.isEmpty() && descNecesidad.isEmpty()) {
            return 1.0;
        }

        int maxLongitud = Math.max(nombreBien.length(), descNecesidad.length());

        LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
        int distancia = levenshtein.apply(nombreBien.toLowerCase(), descNecesidad.toLowerCase());

        return 1.0 - ((double) distancia / maxLongitud);
    }

    private double calcularCoberturaVolumen(Donacion donacion, Necesidad necesidad) {
        double cantDonada = donacion.getBien().getCantidad();
        double cantRequerida = 0.0;

        if (necesidad instanceof NecesidadExtraordinaria extraordinaria) {
            cantRequerida = extraordinaria.getCantidadRequerida();
        } else if (necesidad instanceof NecesidadRecurrente recurrente) {
            cantRequerida = recurrente.getCantidadObjetivoPorPeriodo();
        }

        if (cantRequerida == 0 || cantDonada == 0) {
            return 0.0;
        }

        double r = cantDonada / cantRequerida;
        return (r <= 1.0) ? r : (1.0 / r);
    }

    private double calcularTipoNecesidad(Necesidad necesidad) {
        if (necesidad instanceof NecesidadExtraordinaria) {
            return 1.0;
        } else if (necesidad instanceof NecesidadRecurrente) {
            return 0.5;
        }
        return 0.0;
    }

}
