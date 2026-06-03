package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.CambioEstado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.estados.EnDeposito;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DonacionSegmentada{
    private Long id;
    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();
    private Subcategoria subcategoria;
    private Boolean entregadaAEntidad;
    private EstadoDonacion estadoActual;
    private List<CambioEstado> historial = new ArrayList<>();

    public DonacionSegmentada(Subcategoria subcategoria) {

        this.subcategoria = subcategoria;
        this.estadoActual = new EnDeposito();
        this.historial.add(new CambioEstado(null, this.estadoActual, "Donación registrada en depósito"));
    }

    public void agregarBien(Bien bien){
        this.bienesDelMismoTipo.add(bien);
    }
    public void setId(Long id) { this.id = id; }

    public void cambiarEstado(EstadoDonacion nuevoEstado, String justificacion) {
        this.historial.add(new CambioEstado(this.estadoActual, nuevoEstado, justificacion));
        this.estadoActual = nuevoEstado;
    }

    public String getNombreEstadoActual() { return estadoActual.getNombreEstado(); }

    public void asignar() { estadoActual.asignar(this); }
    public void marcarListaParaEntregar() { estadoActual.marcarListaParaEntregar(this); }
    public void iniciarTraslado() { estadoActual.iniciarTraslado(this); }
    public void entregar() { estadoActual.entregar(this); }
    public void fallarEntrega(String justificacion) { estadoActual.fallarEntrega(this, justificacion); }
    public void marcarVencida() { estadoActual.marcarVencida(this); }

}
