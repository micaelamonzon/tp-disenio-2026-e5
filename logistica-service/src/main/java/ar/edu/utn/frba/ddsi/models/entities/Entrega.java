package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Entrega {
    private Long id;
    private List<Long> donacionesIds; // referencia a donaciones segementadas, de donaciones-service
    private Long entidadBeneficiariaId;
    private Long donanteId; // referencia al donante, de donaciones-service
    private EstadoEntrega estadoActual;
    private LocalDateTime fechaEntrega;
    private List<String> fotosUrl = new ArrayList<>(); // URLs de fotos de recepción
    private String motivoNoRecepcion;
    private String latitud; //de la entidadBenficiaria
    private String longitud; //de la entidadBenficiaria
    private String direccion;  //de la entidadBenficiaria
    private String pesoEnKg;
    private String volumenM3;


    public Entrega(List<Long> donacionesIds, Long entidadBeneficiariaId, Long donanteId) {
        this.entidadBeneficiariaId = entidadBeneficiariaId;
        this.donacionesIds = donacionesIds;
        this.donanteId = donanteId;
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
        this.fechaEntrega = LocalDateTime.now();
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