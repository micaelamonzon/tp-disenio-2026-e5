package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import ar.edu.utn.frba.ddsi.dto.ConfirmarRecepcionDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.dto.EntidadContactoDTO;
import ar.edu.utn.frba.ddsi.dto.MedioDeNotificacionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaContactoDTO;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ar.edu.utn.frba.ddsi.services.NotificacionesPublisher;

import java.util.List;

@Service
public class EntregaServiceImpl {

    private final RepositoryEntregas repositoryEntregas;
    private final DonacionesClient donacionesClient;
    private final NotificacionesPublisher notificacionesPublisher;
    private final String adminContacto;

    public EntregaServiceImpl(RepositoryEntregas repositoryEntregas,
                              DonacionesClient donacionesClient,
                              NotificacionesPublisher notificacionesPublisher,
                              @Value("${notificaciones.admin.contacto}") String adminContacto) {
        this.repositoryEntregas = repositoryEntregas;
        this.donacionesClient = donacionesClient;
        this.notificacionesPublisher = notificacionesPublisher;
        this.adminContacto = adminContacto;
    }

    public Entrega crearEntrega(List<Long> donacionesIds, Long entidadId,List<Long> donantesIds) {
        Entrega entrega = new Entrega(donacionesIds, entidadId);
        donantesIds.forEach(entrega::agregarDonanteId);
        repositoryEntregas.save(entrega);

        return entrega;
    }

    public EntregaDTO crearEntregaDTO(List<Long> donacionesIds, Long entidadId, List<Long> donantesIds) {
        Entrega entrega = new Entrega(donacionesIds, entidadId);
        donantesIds.forEach(entrega::agregarDonanteId);
        repositoryEntregas.save(entrega);
        return this.toDTO(entrega);
    }

    public EntregaDTO iniciarTraslado(String entregaId, String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.iniciarTraslado();
        repositoryEntregas.save(entrega);
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.iniciarTraslado(donacionId, responsableId);
        }
        return toDTO(entrega);
    }

    public List<EntregaDTO> iniciarRuta(List<String> entregaIds, String choferId) {
        List<EntregaDTO> resultado = new java.util.ArrayList<>();
        for (String entregaId : entregaIds) {
            resultado.add(this.iniciarTraslado(entregaId, choferId));
        }
        String aviso = "El+camion+inicio+la+ruta.+Segui+tu+entrega+en:+"
                + "http://localhost:8083/logistica-service/camiones/monitoreo/ruta/camion/1/recorrido";
        for (String entregaId : entregaIds) {
            notificarInteresados(buscar(entregaId), aviso);
        }
        return resultado;
    }

    public EntregaDTO confirmarRecepcion(String entregaId, ConfirmarRecepcionDTO body,
                                         String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.confirmarRecepcion(body.getFotosUrl());
        repositoryEntregas.save(entrega);
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.confirmarEntrega(donacionId, responsableId, body.getFotosUrl());
        }
        String comprobante = "Entrega+realizada+con+exito.+Fecha:+" + entrega.getFechaEntrega()
                + ".+Entrega+nro+" + entrega.getId();
        notificarInteresados(entrega, comprobante);
        return toDTO(entrega);
    }

    public EntregaDTO marcarNoRecibida(String entregaId, ConfirmarRecepcionDTO body,
                                       String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.marcarNoRecibida(body.getMotivo());
        repositoryEntregas.save(entrega);
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.marcarEntregaFallida(donacionId, body.getMotivo(), responsableId);
        }
        String aviso = "Entrega+nro+" + entrega.getId() + "+no+pudo+concretarse.+Motivo:+"
                + body.getMotivo().replace(" ", "+");
        notificarInteresados(entrega, aviso);
        notificacionesPublisher.publicar(adminContacto, aviso, "SMS");
        return toDTO(entrega);
    }

    public EntregaDTO volverAPendiente(String entregaId) {
        Entrega entrega = buscar(entregaId);
        entrega.volverAPendiente();
        repositoryEntregas.save(entrega);
        return toDTO(entrega);
    }

    private void notificarInteresados(Entrega entrega, String mensaje) {
        try {
            EntidadContactoDTO entidad = donacionesClient.obtenerEntidad(entrega.getEntidadBeneficiariaId());
            if (entidad != null && entidad.representantes() != null
                    && !entidad.representantes().isEmpty()
                    && !entidad.representantes().get(0).mediosDeNotificacion().isEmpty()) {
                MedioDeNotificacionDTO medio = entidad.representantes().get(0).mediosDeNotificacion().get(0);
                notificacionesPublisher.publicar(medio.datoDeContacto(), mensaje, medio.tipoDeNotificacion());
            }
        } catch (Exception e) {
            System.out.println("No se pudo notificar a la entidad: " + e.getMessage());
        }
//        // El contacto del donante ya no se resuelve aca, se publica solo el id
//        // y notificaciones-service lo resuelve al consumir el mensaje de la cola
//        if (entrega.getDonanteId() != null) {
//            notificacionesPublisher.publicarParaDonante(entrega.getDonanteId(), mensaje, "SMS");
//        }
    }

    private Entrega buscar(String id) {
        return repositoryEntregas.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada: " + id));
    }

    private EntregaDTO toDTO(Entrega e) {
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
    public List<EntregaDTO> listarEntregas() {
        return repositoryEntregas.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public EntregaDTO obtenerEntrega(String id) {
        return toDTO(buscar(id));
    }
}