package ar.edu.utn.frba.ddsi.models.entities;

import java.util.Map;
import java.util.Set;
//Revisar si esta bien como enum o si se puede aplicar algun patron
//prueba 2.0
public enum EstadoEntrega {
    PENDIENTE,
    EN_CAMINO,
    ENTREGADA,
    NO_RECIBIDA;

    private static final Map<EstadoEntrega, Set<EstadoEntrega>> TRANSICIONES = Map.of(
            PENDIENTE,    Set.of(EN_CAMINO),
            EN_CAMINO,    Set.of(ENTREGADA, NO_RECIBIDA),
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