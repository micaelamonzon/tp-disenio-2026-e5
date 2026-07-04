package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Entrega {
    private Long id;
    private Long donacionSegmentadaId; // referencia a donaciones-service
    private Long entidadBeneficiariaId;
    private Camiones camion;
    private EstadoEntrega estadoActual;
    private LocalDateTime fechaEntrega;
    private List<String> fotosUrl = new ArrayList<>(); // URLs de fotos de recepción
    private String motivoNoRecepcion;

    public Entrega(Long donacionSegmentadaId, Long entidadBeneficiariaId, Camiones camion) {
        this.donacionSegmentadaId = donacionSegmentadaId;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
        this.camion = camion;
        this.estadoActual = EstadoEntrega.PENDIENTE;
        this.fechaEntrega = LocalDateTime.now();
    }

    public void setId(Long id) { this.id = id; }

    public void iniciarTraslado() {
        estadoActual.validarTransicion(EstadoEntrega.EN_CAMINO);
        this.estadoActual = EstadoEntrega.EN_CAMINO;
    }

    public void confirmarRecepcion(List<String> fotos) {
        estadoActual.validarTransicion(EstadoEntrega.ENTREGADA);
        this.fotosUrl = fotos;
        this.estadoActual = EstadoEntrega.ENTREGADA;
    }

    public void marcarNoRecibida(String motivo) {
        estadoActual.validarTransicion(EstadoEntrega.NO_RECIBIDA);
        this.motivoNoRecepcion = motivo;
        this.estadoActual = EstadoEntrega.NO_RECIBIDA;
    }

    public void volverAPendiente() {
        estadoActual.validarTransicion(EstadoEntrega.PENDIENTE);
        this.estadoActual = EstadoEntrega.PENDIENTE;
    }
}