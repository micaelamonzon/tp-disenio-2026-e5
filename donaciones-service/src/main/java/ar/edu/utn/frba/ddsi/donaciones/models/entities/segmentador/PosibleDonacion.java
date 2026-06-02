package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados.EnDeposito;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PosibleDonacion {
    private EstadoDonacion estadoActual;
    private List<CambioEstado> historial;
    private Subcategoria subcategoria;
    private List<Bien> bienes;

    public PosibleDonacion(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
        this.bienes = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.estadoActual = new EnDeposito();
        this.historial.add(new CambioEstado(null, this.estadoActual, "Donación registrada en depósito"));
    }

    public void cambiarEstado(EstadoDonacion nuevoEstado, String justificacion) {
        this.historial.add(new CambioEstado(this.estadoActual, nuevoEstado, justificacion));
        this.estadoActual = nuevoEstado;
    }

    public void asignar() { estadoActual.asignar(this); }
    public void marcarListaParaEntregar() { estadoActual.marcarListaParaEntregar(this); }
    public void iniciarTraslado() { estadoActual.iniciarTraslado(this); }
    public void entregar() { estadoActual.entregar(this); }
    public void fallarEntrega(String justificacion) { estadoActual.fallarEntrega(this, justificacion); }
    public void marcarVencida() { estadoActual.marcarVencida(this); }

    public String getNombreEstadoActual() { return estadoActual.getNombreEstado(); }
}