package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import ar.edu.utn.frba.ddsi.dto.ConfirmarRecepcionDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EntregaServiceImpl {

    // repositorio en memoria de entregas (se pierde al reiniciar el servicio)
    private final Map<Long, Entrega> entregas = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);
    private final DonacionesClient donacionesClient;

    public EntregaServiceImpl(DonacionesClient donacionesClient) {
        this.donacionesClient = donacionesClient;
    }

    // Adopción al nuevo modelo de Entrega: ya no recibe patente ni busca camión,
    // porque la entrega no conoce al camión (la relación quedó Camion -> Ruta ->
    // PuntoDeEntrega -> Entrega, como en el diagrama de clases).
    // Ahora recibe una lista de donaciones en vez de una sola
    public EntregaDTO crearEntrega(List<Long> donacionesIds, Long entidadId) {
        Entrega entrega = new Entrega(donacionesIds, entidadId);
        Long id = contador.getAndIncrement();
        entrega.setId(id);
        entregas.put(id, entrega);
        return toDTO(entrega);
    }

    public EntregaDTO iniciarTraslado(Long entregaId, String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.iniciarTraslado();
        // Como una entrega ahora agrupa varias donaciones, se avisa a
        // donaciones-service el cambio de estado de cada una
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.iniciarTraslado(donacionId, responsableId);
        }
        return toDTO(entrega);
    }

    public EntregaDTO confirmarRecepcion(Long entregaId, ConfirmarRecepcionDTO body,
                                         String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.confirmarRecepcion(body.getFotosUrl());
        // Se notifica a donaciones-service por cada donación de la entrega
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.confirmarEntrega(donacionId, responsableId);
        }
        return toDTO(entrega);
    }

    public EntregaDTO marcarNoRecibida(Long entregaId, ConfirmarRecepcionDTO body,
                                       String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.marcarNoRecibida(body.getMotivo());
        // Se notifica a donaciones-service por cada donación de la entrega
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.marcarEntregaFallida(donacionId, body.getMotivo(), responsableId);
        }
        return toDTO(entrega);
    }

    public EntregaDTO volverAPendiente(Long entregaId) {
        Entrega entrega = buscar(entregaId);
        entrega.volverAPendiente();
        return toDTO(entrega);
    }

    // Eliminamos el Map de camiones y registrarCamion(): solo se usaban para
    // asociar el camión al crear la entrega, y eso ya no corresponde con el
    // nuevo modelo. Los camiones ahora se gestionan desde RepositoryCamiones

    private Entrega buscar(Long id) {
        Entrega e = entregas.get(id);
        if (e == null) throw new RuntimeException("Entrega no encontrada: " + id);
        return e;
    }

    private EntregaDTO toDTO(Entrega e) {
        // Sacamos la patente del camión (la entrega ya no lo conoce) y
        // donacionSegmentadaId pasó a ser la lista donacionesIds
        return new EntregaDTO(
                e.getId(),
                e.getDonacionesIds(),
                e.getEntidadBeneficiariaId(),
                e.getEstadoActual().name(),
                e.getFechaEntrega(),
                e.getFotosUrl(),
                e.getMotivoNoRecepcion()
        );
    }
}