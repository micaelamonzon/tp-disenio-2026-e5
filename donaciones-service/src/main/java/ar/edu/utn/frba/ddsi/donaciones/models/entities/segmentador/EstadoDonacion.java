package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import lombok.Data;

import java.util.Set;
import java.util.Map;


public enum EstadoDonacion {
    EN_DEPOSITO,
    ASIGNACION_REALIZADA,
    LISTA_PARA_ENTREGAR,
    EN_TRASLADO,
    ENTREGADA,
    ENTREGA_FALLIDA,
    VENCIDA,
    INICIO;

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

    public EstadoDonacion esUnEstado(String estadoString){
        if(estadoString.equals(EstadoDonacion.EN_DEPOSITO.name())){
            return EstadoDonacion.EN_DEPOSITO;
        }
        else if(estadoString.equals(EstadoDonacion.ASIGNACION_REALIZADA.name())){
            return EstadoDonacion.ASIGNACION_REALIZADA;
        }
        else if(estadoString.equals(EstadoDonacion.LISTA_PARA_ENTREGAR.name())){
            return EstadoDonacion.LISTA_PARA_ENTREGAR;
        }
        else if(estadoString.equals(EstadoDonacion.EN_TRASLADO.name())){
            return EstadoDonacion.EN_TRASLADO;
        }
        else if(estadoString.equals(EstadoDonacion.ENTREGADA.name())){
            return EstadoDonacion.ENTREGADA;
        }
        else if(estadoString.equals(EstadoDonacion.ENTREGA_FALLIDA.name())){
            return EstadoDonacion.ENTREGA_FALLIDA;
        }
        else if(estadoString.equals(EstadoDonacion.VENCIDA.name())){
            return EstadoDonacion.VENCIDA;
        }
        else if(estadoString.equals(EstadoDonacion.INICIO.name())){
            return EstadoDonacion.INICIO;
        }
        else {
            return null;
        }
    }
}