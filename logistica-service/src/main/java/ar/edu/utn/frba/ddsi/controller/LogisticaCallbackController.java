package ar.edu.utn.frba.ddsi.controller;

import ar.edu.utn.frba.ddsi.dto.CallbackPlanificadorDTO;
import ar.edu.utn.frba.ddsi.dto.PlanningResultDTO;
import ar.edu.utn.frba.ddsi.services.Impl.EntregaServiceImpl;
import ar.edu.utn.frba.ddsi.services.Impl.VerificadorFirmaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/logistica-service")
public class LogisticaCallbackController {

    private final EntregaServiceImpl entregaService;
    private final VerificadorFirmaService firmaVerificador;
    private final ObjectMapper objectMapper;

    public LogisticaCallbackController(
            EntregaServiceImpl entregaService,
            VerificadorFirmaService firmaVerificador,
            ObjectMapper objectMapper) {
        this.entregaService = entregaService;
        this.firmaVerificador = firmaVerificador;
        this.objectMapper = objectMapper;
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

        switch (callback.eventType()) {
            case "routing.completed" -> procesarRutasCompletadas(callback.data());
            case "routing.failed" -> {
                System.err.println("Planificación fallida: " + callback.error().message());
            }
            default -> {
                System.err.println("Tipo de evento desconocido: " + callback.eventType());
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok().build();
    }

    private void procesarRutasCompletadas(PlanningResultDTO data) {
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
