package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Document(collection = "entregas")
public class Entrega {
    @Id
    private String id;
    private List<Long> donacionesIds = new ArrayList<>();
    private Long entidadBeneficiariaId;
    private Long donanteId;
    private EstadoEntrega estadoActual;
    private LocalDateTime fechaEntrega;
    private List<String> fotosUrl = new ArrayList<>();
    private String motivoNoRecepcion;
    private String latitud;
    private String longitud;
    private String direccion;

    public Entrega(List<Long> donacionesIds, Long entidadBeneficiariaId, Long donanteId) {
        this.donacionesIds = donacionesIds;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
        this.donanteId = donanteId;
        this.estadoActual = EstadoEntrega.PENDIENTE;
        this.fechaEntrega = LocalDateTime.now();
    }

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