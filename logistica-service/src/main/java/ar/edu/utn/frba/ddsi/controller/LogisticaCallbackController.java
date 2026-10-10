package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.dto.CallbackPlanificadorDTO;
import ar.edu.utn.frba.ddsi.dto.PlanningResultDTO;
import ar.edu.utn.frba.ddsi.repository.impl.SolicitudPlanificacionRepository;
import ar.edu.utn.frba.ddsi.services.Impl.EntregaServiceImpl;
import ar.edu.utn.frba.ddsi.services.Impl.SolicitudPlanificacionServiceImpl;
import ar.edu.utn.frba.ddsi.services.Impl.VerificadorFirmaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/logistica-service")
public class LogisticaCallbackController {

    private final EntregaServiceImpl entregaService;
    private final VerificadorFirmaService firmaVerificador;
    private final ObjectMapper objectMapper;
    private final SolicitudPlanificacionRepository solicitudRepository;
    private final SolicitudPlanificacionServiceImpl solicitudService;

    public LogisticaCallbackController(
            EntregaServiceImpl entregaService,
            VerificadorFirmaService firmaVerificador,
            ObjectMapper objectMapper,
            SolicitudPlanificacionRepository solicitudRepository,
            SolicitudPlanificacionServiceImpl solicitudService) {
        this.entregaService = entregaService;
        this.firmaVerificador = firmaVerificador;
        this.objectMapper = objectMapper;
        this.solicitudRepository = solicitudRepository;
        this.solicitudService = solicitudService;
    }

    @PostMapping("/callback-planificacion")
    public ResponseEntity<Void> recibirCallback(
            @RequestHeader("X-Signature") String firma,
            @RequestBody String bodyRaw) {

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

    private void actualizarSolicitud(String requestId, PlanningResultDTO resultado) {
        solicitudRepository.findById(requestId).ifPresent(s -> {
            s.completar(objectMapper.writeValueAsString(resultado));
            solicitudRepository.save(s);
        });
    }

    private void marcarSolicitudFallida(String requestId, String mensaje) {
        if (requestId == null) return;
        solicitudRepository.findById(requestId).ifPresent(s -> {
            s.fallar(mensaje);
            solicitudRepository.save(s);
        });
    }

    private void procesarRutasCompletadas(PlanningResultDTO data) {
        if (data == null || data.routes() == null) return;

        Set<Long> donacionesYaVistas = new HashSet<>();
        Set<String> truckIdsYaVistos = new HashSet<>();

        data.routes().forEach(route -> {
            List<Long> donacionesIds = route.stops().stream()
                    .map(stop -> extraerDonacionId(stop.deliveryCode()))
                    .filter(Objects::nonNull)
                    .toList();

            if (donacionesIds.isEmpty()) {
                System.err.println("Ruta " + route.assignedRouteId() + " sin donaciones válidas, se omite.");
                return;
            }

            entregaService.crearEntrega(donacionesIds, null, null);
            System.out.println("Entrega creada para camión: " + route.truckId()
                    + " con " + donacionesIds.size() + " donaciones");
        });

        if (data.unassignedDeliveries() != null && !data.unassignedDeliveries().isEmpty()) {
            System.out.println("Donaciones sin asignar: " + data.unassignedDeliveries());
        }
    }

    private Long extraerDonacionId(String deliveryCode) {
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
}
