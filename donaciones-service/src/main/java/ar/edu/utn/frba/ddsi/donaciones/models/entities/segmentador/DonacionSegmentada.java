package ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DonacionSegmentada {
    private Long id;
    private Long idDelDonante;
    private TipoDonante tipoDonante;
    private ArrayList<Bien> bienesDelMismoTipo = new ArrayList<>();
    private Subcategoria subcategoria;
    private EstadoDonacion estadoActual;
    private List<CambioEstado> historial = new ArrayList<>();
    private LocalDateTime fechaDeIngreso;
    private Boolean donacionEntregada;
    private Long entidadBeneficiariaId;
    private LocalDate fechaEntrega;

    public DonacionSegmentada(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
        this.estadoActual = EstadoDonacion.EN_DEPOSITO;
        historial.add(new CambioEstado(null, EstadoDonacion.EN_DEPOSITO,
                "Donación registrada en depósito", "SISTEMA"));
    }



    public void agregarBien(Bien bien) {
        this.bienesDelMismoTipo.add(bien);
    }

    public void cambiarEstado(EstadoDonacion nuevo, String justificacion, String responsableId) {
        this.estadoActual.validarTransicion(nuevo);
        historial.add(new CambioEstado(this.estadoActual, nuevo, justificacion, responsableId));
        this.estadoActual = nuevo;
    }


}