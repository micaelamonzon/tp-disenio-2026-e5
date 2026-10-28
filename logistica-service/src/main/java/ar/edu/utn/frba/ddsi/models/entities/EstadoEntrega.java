package ar.edu.utn.frba.ddsi.models.entities;

import java.util.Map;
import java.util.Set;


public enum EstadoEntrega {
    PENDIENTE,
    EN_TRASLADO,
    ENTREGADA,
    NO_RECIBIDA;

    private static final Map<EstadoEntrega, Set<EstadoEntrega>> TRANSICIONES = Map.of(
            PENDIENTE,    Set.of(EN_TRASLADO),
            EN_TRASLADO,    Set.of(ENTREGADA, NO_RECIBIDA),
            ENTREGADA,    Set.of(),
            NO_RECIBIDA,  Set.of(PENDIENTE)
    );

    public void validarTransicion(EstadoEntrega destino) {
        if (!TRANSICIONES.get(this).contains(destino)) {
            throw new IllegalStateException(
                    "Transición inválida: " + this.name() + " → " + destino.name()
            );
        }
    }
}