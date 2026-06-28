package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import java.util.Set;
import java.util.Map;

public enum EstadoDonacion {
    EN_DEPOSITO,
    ASIGNACION_REALIZADA,
    LISTA_PARA_ENTREGAR,
    EN_TRASLADO,
    ENTREGADA,
    ENTREGA_FALLIDA,
    VENCIDA;

    //Transiciones validas seteadas
    private static final Map<EstadoDonacion, Set<EstadoDonacion>> TRANSICIONES_VALIDAS = Map.of(
            EN_DEPOSITO,          Set.of(ASIGNACION_REALIZADA, VENCIDA),
            ASIGNACION_REALIZADA, Set.of(LISTA_PARA_ENTREGAR, VENCIDA),
            LISTA_PARA_ENTREGAR,  Set.of(EN_TRASLADO, VENCIDA),
            EN_TRASLADO,          Set.of(ENTREGADA, ENTREGA_FALLIDA),
            ENTREGA_FALLIDA,      Set.of(EN_DEPOSITO),
            ENTREGADA,            Set.of(),
            VENCIDA,              Set.of()
    );

    public boolean puedeTransicionarA(EstadoDonacion destino) {
        return TRANSICIONES_VALIDAS.get(this).contains(destino);
    }

    public void validarTransicion(EstadoDonacion destino) {
        if (!puedeTransicionarA(destino)) {
            throw new IllegalStateException(
                    "Transición inválida: " + this.name() + " → " + destino.name()
            );
        }
    }
}