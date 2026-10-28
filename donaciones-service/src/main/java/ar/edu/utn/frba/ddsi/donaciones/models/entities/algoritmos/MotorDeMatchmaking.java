package ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public class MotorDeMatchmaking {
    private Strategy_AlgoritmosMatchmaking estrategiaActual;

//    public MotorDeMatchmaking(Strategy_AlgoritmosMatchmaking estrategiaInicial) {
//        this.estrategiaActual = estrategiaInicial;
//    }

    public List<Necesidad> generarRanking(DonacionSegmentada donacion, List<Necesidad> todasLasNecesidades) {
        if (this.estrategiaActual == null) {
            throw new IllegalStateException("Se debe configurar una estrategia de matchmaking antes de generar el ranking.");
        }

        // Validamos que la donación tenga un bien asignado para poder filtrar
        if (donacion.getSubcategoria() == null) {
            throw new IllegalArgumentException("La donación debe contener un bien con una subcategoría válida.");
        }

        return todasLasNecesidades.stream()
                //Solo se evalúan necesidades no satisfechas
                .filter(necesidad -> {
                    return !necesidad.estaSatisfecha();
                })
                // Solo se evalúan contra necesidades de la misma subcategoría
                .filter(necesidad -> mismaSubcategoria(necesidad.getSubcategoria(), donacion.getSubcategoria()))
                //Calculamos el puntaje y las ordenamos de mayor a menor
                .sorted(Comparator.comparingDouble((Necesidad necesidad) ->
                        estrategiaActual.calcularPuntaje(donacion, necesidad)).reversed())

                .limit(10).collect(Collectors.toList());
    }

    private boolean mismaSubcategoria(Subcategoria deNecesidad, Subcategoria deDonacion) {
        if (deNecesidad == null || deDonacion == null) return false;
        if (deNecesidad.getId() != null && deDonacion.getId() != null) {
            return deNecesidad.getId().equals(deDonacion.getId());
        }
        return deNecesidad == deDonacion;
    }
}
