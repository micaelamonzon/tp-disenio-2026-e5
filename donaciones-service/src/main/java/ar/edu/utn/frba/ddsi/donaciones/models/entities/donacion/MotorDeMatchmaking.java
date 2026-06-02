package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Setter
public class MotorDeMatchmaking {
    private Strategy_AlgoritmosMatchmaking estrategiaActual;

    public MotorDeMatchmaking(Strategy_AlgoritmosMatchmaking estrategiaInicial) {
        this.estrategiaActual = estrategiaInicial;
    }

    public List<Necesidad> generarRanking(Donacion donacion, List<Necesidad> todasLasNecesidades) {
        if (this.estrategiaActual == null) {
            throw new IllegalStateException("Se debe configurar una estrategia de matchmaking antes de generar el ranking.");
        }

        // Validamos que la donación tenga un bien asignado para poder filtrar
        if (donacion.getBien() == null || donacion.getBien().getSubcategoria() == null) {
            throw new IllegalArgumentException("La donación debe contener un bien con una subcategoría válida.");
        }

        return todasLasNecesidades.stream()
                //Solo se evalúan necesidades no satisfechas
                .filter(necesidad -> !necesidad.estaSatisfecha())
                // Solo se evalúan contra necesidades de la misma subcategoría
                .filter(necesidad -> necesidad.getSubcategoria() != null &&
                        necesidad.getSubcategoria().equals(donacion.getBien().getSubcategoria()))
                //Calculamos el puntaje y las ordenamos de mayor a menor
                .sorted(Comparator.comparingDouble((Necesidad necesidad) ->
                        estrategiaActual.calcularPuntaje(donacion, necesidad)).reversed())

                .limit(10).collect(Collectors.toList());
    }
}
