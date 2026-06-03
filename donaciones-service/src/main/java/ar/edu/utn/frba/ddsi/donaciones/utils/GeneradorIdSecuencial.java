package ar.edu.utn.frba.ddsi.donaciones.utils;

import org.springframework.stereotype.Component;

@Component
public class GeneradorIdSecuencial {
    private long siguiente;

    public GeneradorIdSecuencial() {
        this(1L);
    }

    public GeneradorIdSecuencial(long valorInicial) {
        this.siguiente = valorInicial;
    }
    public synchronized long siguiente() {
        return siguiente++;
    }
}
