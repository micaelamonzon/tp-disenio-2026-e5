package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.EntidadBeneficiaria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrioridadSubatendidos implements Strategy_AlgoritmosMatchmaking {
    private final double alpha; // Factor de suavizado

    public PrioridadSubatendidos(@Value("${alpha}") double alpha) {
        if (alpha <= 0) {
            throw new IllegalArgumentException("El factor de suavizado 'alpha' debe ser mayor a 0");
        }
        this.alpha = alpha;
    }

    @Override
    public double calcularPuntaje(Donacion donacion, Necesidad necesidad) {
        EntidadBeneficiaria entidad = necesidad.getEntidadBeneficiaria();

        if (entidad == null) {
            return 0.0;
        }

        long cantDonacionesUltimoTrimestre = entidad.obtenerCantidadDonacionesUltimoTrimestre();

        //Fórmula Ssub = 1 / (1 + alpha * CantDonacionesTrimestre)
        return 1.0 / (1.0 + (this.alpha * cantDonacionesUltimoTrimestre));
    }

}
