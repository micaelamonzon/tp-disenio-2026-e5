package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.Donacion;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryCamiones;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryDonacion;
import ar.edu.utn.frba.ddsi.services.DonacionesClient;
import ar.edu.utn.frba.ddsi.dto.ConfirmarRecepcionDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.dto.EntidadContactoDTO;
import ar.edu.utn.frba.ddsi.dto.MedioDeNotificacionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ar.edu.utn.frba.ddsi.services.NotificacionesPublisher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntregaServiceImpl {

    private final RepositoryEntregas repositoryEntregas;
    private final DonacionesClient donacionesClient;
    private final NotificacionesPublisher notificacionesPublisher;
    private final RepositoryCamiones repositoryCamiones;
    private final String adminContacto;
    private final RepositoryDonacion repositoryDonacion;

    public EntregaServiceImpl(RepositoryEntregas repositoryEntregas,
                              DonacionesClient donacionesClient,
                              NotificacionesPublisher notificacionesPublisher,
                              RepositoryCamiones repositoryCamiones,
                              RepositoryDonacion repositoryDonacion,
                              @Value("${notificaciones.admin.contacto}") String adminContacto) {
        this.repositoryEntregas = repositoryEntregas;
        this.donacionesClient = donacionesClient;
        this.notificacionesPublisher = notificacionesPublisher;
        this.adminContacto = adminContacto;
        this.repositoryCamiones = repositoryCamiones;
        this.repositoryDonacion = repositoryDonacion;
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

    public List<EntregaDTO> iniciarTraslado(Long idDonante, String responsable) {
        List<Entrega> entregas = buscarEntregas(idDonante);

        entregas.forEach(entrega -> {
            entrega.iniciarTraslado();
            repositoryEntregas.save(entrega);
            this.persistirCambiosEnDonaciones(entrega,responsable);
        });

        return entregas.stream().map(entrega -> this.toDTO(entrega)).collect(Collectors.toList());
    }

    public void persistirCambiosEnDonaciones(Entrega entrega,String responsable) {
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.iniciarTraslado(donacionId, responsable);
        }
    }
//    public List<EntregaDTO> iniciarRuta(List<String> entregaIds, String choferId) {
//        List<EntregaDTO> resultado = new java.util.ArrayList<>();
//        for (String entregaId : entregaIds) {
//            resultado.add(this.iniciarTraslado(entregaId, choferId));
//        }
//        String aviso = "El+camion+inicio+la+ruta.+Segui+tu+entrega+en:+"
//                + "http://localhost:8083/logistica-service/camiones/monitoreo/ruta/camion/1/recorrido";
//        for (String entregaId : entregaIds) {
//            notificarInteresados(buscar(entregaId), aviso);
//        }
//        return resultado;
//    }

    public EntregaDTO confirmarRecepcion(String entregaId, ConfirmarRecepcionDTO body,
                                         String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.confirmarRecepcion(body.getFotosUrl());
        repositoryEntregas.save(entrega);
        this.liberarCamionSiTermino(entrega);
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.confirmarEntrega(donacionId, responsableId, body.getFotosUrl());
        }
        String comprobante = "Entrega realizada con éxito. Fecha: " + entrega.getFechaEntrega()
                + ". Entrega nro " + entrega.getId();
        notificarInteresados(entrega, comprobante);
        return toDTO(entrega);
    }

    private void liberarCamionSiTermino(Entrega entrega) {
        String camionId = entrega.getCamionId();
        if (camionId == null) return;

        boolean quedanActivas = repositoryEntregas.findByCamionId(camionId).stream()
                .anyMatch(e -> e.getEstadoActual() == EstadoEntrega.PENDIENTE
                        || e.getEstadoActual() == EstadoEntrega.EN_TRASLADO);
        if (quedanActivas) return;

        repositoryCamiones.findById(camionId).ifPresent(camion -> {
            camion.getPosiciones().clear();
            camion.setDisponible(true);
            repositoryCamiones.save(camion);
        });
    }

    public EntregaDTO marcarNoRecibida(String entregaId, ConfirmarRecepcionDTO body,
                                       String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.marcarNoRecibida(body.getMotivo());
        repositoryEntregas.save(entrega);
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.marcarEntregaFallida(donacionId, body.getMotivo(), responsableId);
        }
        String aviso = "Entrega nro " + entrega.getId() + " no pudo concretarse. Motivo: "
                + body.getMotivo();
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
                notificacionesPublisher.publicarParaEntidad(entrega.getEntidadBeneficiariaId(),
                        medio.datoDeContacto(), mensaje, medio.tipoDeNotificacion());
            }
        } catch (Exception e) {
            System.out.println("No se pudo notificar a la entidad: " + e.getMessage());
        }

        // Un aviso por cada donante de la entrega (sin repetir si donó varias cosas)
        // Solo se manda el id y el tipo (HUMANO/JURIDICO), que sale de la donación guardada en logística
        // notificaciones-service resuelve el contacto y su medio preferido
        Set<String> yaAvisados = new HashSet<>();
        for (Long donacionId : entrega.getDonacionesIds()) {
            Donacion donacion = repositoryDonacion.findByIdEnDonaciones(donacionId);
            if (donacion == null || donacion.getDonanteId() == null) continue;

            String tipo = donacion.getTipoDeDonante() != null
                    ? donacion.getTipoDeDonante().name() : "HUMANO";
            if (yaAvisados.add(tipo + "-" + donacion.getDonanteId())) {
                notificacionesPublisher.publicarParaDonante(donacion.getDonanteId(), mensaje, null, tipo);
            }
        }
    }

    private List<Entrega> buscarEntregas(Long donanteId){
        List<Long> idsDonantes = new ArrayList<Long>();
        idsDonantes.add(donanteId);
        return repositoryEntregas.findByDonantesId(idsDonantes);
    }

    public void iniciarTrasladoDeEntrega(String entregaId, String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.iniciarTraslado();
        repositoryEntregas.save(entrega);
        try {
            persistirCambiosEnDonaciones(entrega, responsableId);
        } catch (Exception e) {
            System.out.println("No se pudo avisar a donaciones (iniciarTraslado, entrega "
                    + entregaId + "): " + e.getMessage());
        }
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

    public List<EntregaDTO> listarEntregasDeEntidadBeneficiaria(Long idEntidadBeneficiaria){
        return repositoryEntregas.findByEntidadBeneficiariaId(idEntidadBeneficiaria).stream().map(this::toDTO)
                .toList();
    }

    public EntregaDTO obtenerEntrega(String id) {
        return toDTO(buscar(id));
    }
}