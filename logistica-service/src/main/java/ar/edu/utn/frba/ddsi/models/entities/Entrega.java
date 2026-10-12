package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "entregas")
public class Entrega {
    @Id
    private String id;
    private List<Long> donacionesIds = new ArrayList<>();
    private Long entidadBeneficiariaId;
    private String camionId;
    private Integer indiceParada;
    private List<Long> donantesId = new ArrayList<>();
    private EstadoEntrega estadoActual;
    private LocalDateTime fechaEntrega;
    private List<String> fotosUrl = new ArrayList<>();
    private String motivoNoRecepcion;
    private String latitud; // de la entidad
    private String longitud; // de la entidad
    private String direccion; // de la entidad

    public Entrega(List<Long> donacionesIds, Long entidadBeneficiariaId) {
        this.donacionesIds = donacionesIds;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
        this.estadoActual = EstadoEntrega.PENDIENTE;
        this.fechaEntrega = LocalDateTime.now();

    }

    public void agregarDonanteId(Long id) {
        if (id != null && !this.donantesId.contains(id)) {
            this.donantesId.add(id);
        }
    }

    public void iniciarTraslado() {
        estadoActual.validarTransicion(EstadoEntrega.EN_TRASLADO);
        this.estadoActual = EstadoEntrega.EN_TRASLADO;
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