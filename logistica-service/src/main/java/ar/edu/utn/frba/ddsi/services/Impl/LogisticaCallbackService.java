package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.CallbackPlanificadorDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.dto.PlanningResultDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Donacion;
import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import ar.edu.utn.frba.ddsi.models.entities.PuntosDeEntrega;
import ar.edu.utn.frba.ddsi.models.entities.Ruta;
import ar.edu.utn.frba.ddsi.repository.RepositoryCamiones;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryDonacion;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryRutas;
import ar.edu.utn.frba.ddsi.repository.impl.SolicitudPlanificacionRepository;
import ar.edu.utn.frba.ddsi.services.ILogisticaCallbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LogisticaCallbackService implements ILogisticaCallbackService {

    private final EntregaServiceImpl entregaService;
    private final VerificadorFirmaService firmaVerificador;
    private final ObjectMapper objectMapper;
    private final SolicitudPlanificacionRepository solicitudRepository;
    private final SolicitudPlanificacionServiceImpl solicitudService;
    private final RepositoryCamiones repositoryCamiones;
    private final RepositoryRutas repositoryRutas;
    private final DonacionesClient donacionesClient;
    private final RepositoryDonacion repositoryDonacion;
    private final RepositoryEntregas repositoryEntregas;

    public LogisticaCallbackService(
            EntregaServiceImpl entregaService,
            VerificadorFirmaService firmaVerificador,
            ObjectMapper objectMapper,
            SolicitudPlanificacionRepository solicitudRepository,
            SolicitudPlanificacionServiceImpl solicitudService,
            RepositoryCamiones repositoryCamiones,
            RepositoryRutas repositoryRutas,
            DonacionesClient donacionesClient,
            RepositoryDonacion repositoryDonacion,
            RepositoryEntregas repositoryEntregas) {
        this.entregaService = entregaService;
        this.firmaVerificador = firmaVerificador;
        this.objectMapper = objectMapper;
        this.solicitudRepository = solicitudRepository;
        this.solicitudService = solicitudService;
        this.repositoryCamiones = repositoryCamiones;
        this.repositoryRutas = repositoryRutas;
        this.donacionesClient = donacionesClient;
        this.repositoryDonacion = repositoryDonacion;
        this.repositoryEntregas = repositoryEntregas;
    }


    public ResponseEntity<Void> recibirCallback(String firma, String bodyRaw) {

        if (!firmaVerificador.verificar(firma, bodyRaw)) { // verifico q firma ok
            System.err.println("Firma inválida — request rechazado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CallbackPlanificadorDTO callback;
        try {
            callback = objectMapper.readValue(bodyRaw, CallbackPlanificadorDTO.class);
        } catch (Exception e) {
            System.err.println("Error al parsear callback: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        if (SolicitudPlanificacionServiceImpl.yaFueProcesado(callback.requestId())) {
            System.out.println("Callback para requestId=" + callback.requestId() + " ya procesado o desconocido, se ignora.");
            return ResponseEntity.ok().build();
        }


        switch (callback.eventType()) {
            case "routing.completed" -> {
                procesarRutasCompletadas(callback.data());
                actualizarSolicitud(callback.requestId(), callback.data());
                SolicitudPlanificacionServiceImpl.marcarComoCompletada(callback.requestId(), bodyRaw);
            }
            case "routing.failed" -> {
                String mensaje = callback.error() != null ? callback.error().message() : "Sin detalle";
                System.err.println("Planificación fallida (requestId=" + callback.requestId() + "): " + mensaje);
                SolicitudPlanificacionServiceImpl.marcarComoFallida(callback.requestId(), mensaje);
            }
            default -> {
                System.err.println("Tipo de evento desconocido: " + callback.eventType());
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().build();
    }

    public void actualizarSolicitud(String requestId, PlanningResultDTO resultado) {
        solicitudRepository.findById(requestId).ifPresent(s -> {
            s.completar(objectMapper.writeValueAsString(resultado));
            solicitudRepository.save(s);
        });
    }

    public void marcarSolicitudFallida(String requestId, String mensaje) {
        if (requestId == null) return;
        solicitudRepository.findById(requestId).ifPresent(s -> {
            s.fallar(mensaje);
            solicitudRepository.save(s);
        });
    }

    public void procesarRutasCompletadas(PlanningResultDTO data) {
        if (data == null || data.routes() == null) return;

        data.routes().forEach(route -> {
            List<Long> donacionesIds = route.stops().stream()
                    .map(stop -> extraerDonacionId(stop.deliveryCode()))
                    .filter(Objects::nonNull)
                    .toList();

            if (donacionesIds.isEmpty()) {
                System.err.println("Ruta " + route.assignedRouteId() + " sin donaciones válidas, se omite.");
                return;
            }

            List<Donacion> donacionesAentregar = this.buscarDonaciones(donacionesIds);

            Map<Long, List<Donacion>> porEntidad = donacionesAentregar.stream()
                    .filter(Objects::nonNull)
                    .filter(d -> d.getEntidadBeneficiariaId() != null)
                    .collect(Collectors.groupingBy(Donacion::getEntidadBeneficiariaId,
                            LinkedHashMap::new, Collectors.toList()));

            List<Entrega> entregas = new ArrayList<>();

            porEntidad.forEach((entidadId, donaciones) -> {
                List<Long> idsDonaciones = donaciones.stream()
                        .map(Donacion::getIdEnDonaciones)
                        .toList();

                List<Long> donantesIds = donaciones.stream()
                        .map(Donacion::getDonanteId)
                        .toList();

                Entrega entrega = entregaService.crearEntrega(idsDonaciones, entidadId,donantesIds);
                entrega.setCamionId(route.truckId());
                entregas.add(entrega);
            });

            this.setearLatYlong(entregas);

            System.out.println("Entrega creada para camión: " + route.truckId()
                    + " con " + donacionesIds.size() + " donaciones");

            Camion camion = repositoryCamiones.findById(route.truckId()).orElse(null);

            if (camion == null) {
                System.err.println("No se encontró camión con truckId=" + route.truckId());
            }

            Ruta ruta = new Ruta();
            ruta.setCamionId(camion != null ? camion.getId() : null);
            ruta.setFecha(LocalDate.now().plusDays(1));
            ruta.setHoraEstimadaInicio(OffsetDateTime.parse(route.estimatedStartTime()).toLocalTime());
            ruta.setHoraEstimadaFin(OffsetDateTime.parse(route.estimatedEndTime()).toLocalTime());
            ruta.setDistanciaTotal(route.totalDistanceKm() != null
                    ? route.totalDistanceKm() : null);
            ruta.setDuracionTotalMins(route.totalDurationMins());
            ruta.setPuntosDeEntrega(entregas.stream().map(en -> {
                PuntosDeEntrega p = new PuntosDeEntrega();
                p.setId(UUID.randomUUID().toString());
                p.setOrden((entregas.indexOf(en) + 1));
                p.setEntregaId(en.getId());
                p.setEntidadBeneficiariaId(en.getEntidadBeneficiariaId());
                return p;
            }).toList());

            Ruta guardada = repositoryRutas.save(ruta);

            if (camion != null) {
                camion.setRutaId(guardada.getId());
                repositoryCamiones.save(camion);
            }


            for(Long donacionId : donacionesIds) {
                try {
                    donacionesClient.marcarComoListaParaEntregar(donacionId);
                } catch (Exception e) {
                    System.err.println("No se pudo marcar la donación " + donacionId
                            + " como lista para entregar: " + e.getMessage());
                }
            }
        });

        if (data.unassignedDeliveries() != null && !data.unassignedDeliveries().isEmpty()) {
            System.out.println("Donaciones sin asignar: " + data.unassignedDeliveries());
        }
    }

    public Long extraerDonacionId(String deliveryCode) {
        if (deliveryCode == null) return null;
        // Extrae la parte numérica final, sin importar el prefijo (DEL-, ENT-, etc.)
        String soloNumeros = deliveryCode.replaceAll("[^0-9]", "");
        try {
            return soloNumeros.isEmpty() ? null : Long.parseLong(soloNumeros);
        } catch (NumberFormatException e) {
            System.err.println("deliveryCode con formato inesperado: " + deliveryCode);
            return null;
        }
    }

    public List<Donacion> buscarDonaciones(List<Long> donacionesIds){
        List<Donacion> donaciones = new ArrayList<>();
        donacionesIds.forEach(d -> donaciones.add(this.repositoryDonacion.findByIdEnDonaciones((d))));
        return donaciones;
    }

    public void setearLatYlong(List<Entrega> entregas){
        entregas.forEach(entrega -> {
            Long donacionId = entrega.getDonacionesIds().get(0);
            Donacion donacion = this.repositoryDonacion.findByIdEnDonaciones(donacionId);
            entrega.setDireccion(donacion.getDestino());
            entrega.setLatitud(donacion.getLatitud().toString());
            entrega.setLongitud(donacion.getLongitud().toString());
        });
        this.repositoryEntregas.saveAll(entregas);
    }

    
}
