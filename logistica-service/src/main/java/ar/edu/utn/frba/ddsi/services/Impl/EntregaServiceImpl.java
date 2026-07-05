package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import ar.edu.utn.frba.ddsi.dto.ConfirmarRecepcionDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EntregaServiceImpl {

    private final Map<Long, Entrega> entregas = new HashMap<>();
    private final Map<String, Camion> camiones = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);
    private final DonacionesClient donacionesClient;

    public EntregaServiceImpl(DonacionesClient donacionesClient) {
        this.donacionesClient = donacionesClient;
    }

    public EntregaDTO crearEntrega(List<Long> donacionesIds, Long entidadId, String patente) {
        Camion camion = camiones.get(patente);
        if (camion == null) throw new RuntimeException("Camión no encontrado: " + patente);
        Entrega entrega = new Entrega(donacionesIds, entidadId, camion);
        Long id = contador.getAndIncrement();
        entrega.setId(id);
        entregas.put(id, entrega);
        return toDTO(entrega);
    }

    public EntregaDTO iniciarTraslado(Long entregaId, String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.iniciarTraslado();
        // avisa a donaciones-service
        donacionesClient.iniciarTraslado(entrega.getDonacionSegmentadaId(), responsableId);
        return toDTO(entrega);
    }

    public EntregaDTO confirmarRecepcion(Long entregaId, ConfirmarRecepcionDTO body,
                                         String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.confirmarRecepcion(body.getFotosUrl());
        // avisa a donaciones-service
        donacionesClient.confirmarEntrega(entrega.getDonacionSegmentadaId(), responsableId);
        return toDTO(entrega);
    }

    public EntregaDTO marcarNoRecibida(Long entregaId, ConfirmarRecepcionDTO body,
                                       String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.marcarNoRecibida(body.getMotivo());
        // avisa a donaciones-service
        donacionesClient.marcarEntregaFallida(
                entrega.getDonacionSegmentadaId(), body.getMotivo(), responsableId);
        return toDTO(entrega);
    }

    public EntregaDTO volverAPendiente(Long entregaId) {
        Entrega entrega = buscar(entregaId);
        entrega.volverAPendiente();
        return toDTO(entrega);
    }

    public void registrarCamion(Camion camion) {
        camiones.put(camion.getPatente(), camion);
    }

    private Entrega buscar(Long id) {
        Entrega e = entregas.get(id);
        if (e == null) throw new RuntimeException("Entrega no encontrada: " + id);
        return e;
    }

    private EntregaDTO toDTO(Entrega e) {
        return new EntregaDTO(
                e.getId(),
                e.getDonacionSegmentadaId(),
                e.getEntidadBeneficiariaId(),
                e.getCamion().getPatente(),
                e.getEstadoActual().name(),
                e.getFechaEntrega(),
                e.getFotosUrl(),
                e.getMotivoNoRecepcion()
        );
    }
}