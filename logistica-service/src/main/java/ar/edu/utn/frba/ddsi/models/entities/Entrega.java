package ar.edu.utn.frba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "entrega")
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "entrega_donacion", joinColumns = @JoinColumn(name = "entrega_id"))
    @Column(name = "donacion_id")
    private List<Long> donacionesIds = new ArrayList<>();

    @Column(name = "entidad_beneficiaria_id")
    private Long entidadBeneficiariaId;

    @Column(name = "donante_id")
    private Long donanteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual")
    private EstadoEntrega estadoActual;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @ElementCollection
    @CollectionTable(name = "entrega_fotos", joinColumns = @JoinColumn(name = "entrega_id"))
    @Column(name = "foto_url")
    private List<String> fotosUrl = new ArrayList<>();

    @Column(name = "motivo_no_recepcion")
    private String motivoNoRecepcion;

    @Column(name = "latitud")
    private String latitud;

    @Column(name = "longitud")
    private String longitud;

    @Column(name = "direccion")
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