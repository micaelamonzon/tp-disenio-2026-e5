package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.CambioEstado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DonacionSegmentada {
    private Long id;
    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();
    private Subcategoria subcategoria;
    private EstadoDonacion estadoActual;
    private List<CambioEstado> historial = new ArrayList<>();

    public DonacionSegmentada(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
        this.estadoActual = EstadoDonacion.EN_DEPOSITO;
        historial.add(new CambioEstado(null, EstadoDonacion.EN_DEPOSITO,
                "Donación registrada en depósito", "SISTEMA"));
    }

    public void setId(Long id) { this.id = id; }

    public void agregarBien(Bien bien) {
        this.bienesDelMismoTipo.add(bien);
    }

    public void cambiarEstado(EstadoDonacion nuevo, String justificacion, String responsableId) {
        this.estadoActual.validarTransicion(nuevo);
        historial.add(new CambioEstado(this.estadoActual, nuevo, justificacion, responsableId));
        this.estadoActual = nuevo;
    }

    public String getNombreEstadoActual() {
        return estadoActual.name();
    }
}