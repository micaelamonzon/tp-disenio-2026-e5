package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroAdminRequestDTO;
import ar.edu.utn.frba.ddsi.services.DonacionesApiService;
import ar.edu.utn.frba.ddsi.services.RegistroAdminService;
import ar.edu.utn.frba.ddsi.services.RegistroAdminService.DonacionFallidaException;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Endpoints JSON que consume admin.js (la vista "Registrar donaciones").
// Devuelven 401 en vez de redirigir, porque los llama fetch() y no el navegador.
@RestController
@RequestMapping("/personaAdministradora")
public class AdminRegistroController {

    private static final Logger log = LoggerFactory.getLogger(AdminRegistroController.class);

    private final RegistroAdminService registroService;
    private final DonacionesApiService donacionesApi;

    public AdminRegistroController(RegistroAdminService registroService, DonacionesApiService donacionesApi) {
        this.registroService = registroService;
        this.donacionesApi = donacionesApi;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody RegistroAdminRequestDTO req) {
        try {
            var r = registroService.registrar(req);
            return ResponseEntity.ok(Map.of(
                    "donanteId", r.donanteId(), "tipo", r.tipo(),
                    "donaciones", r.donaciones(), "avisos", r.avisos()));
        } catch (SesionExpiradaException e) {
            return error(401, "Tu sesión expiró.");
        } catch (DonacionFallidaException e) {
            log.warn("Falló la donación del donante {}", e.getDonanteId(), e);
            return ResponseEntity.status(502).body(Map.of(
                    "error", e.getMessage(), "donanteId", e.getDonanteId(),
                    "tipo", e.getTipo(), "avisos", e.getAvisos()));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (Exception e) {
            log.warn("No se pudo completar el registro", e);
            return error(502, e.getMessage());
        }
    }

    // Búsqueda de donante existente: antes traía todo al navegador; ahora filtra el front
    @GetMapping("/donantes")
    public ResponseEntity<?> buscarDonantes(@RequestParam String q) {
        try {
            String query = q.trim().toLowerCase();
            List<Map<String, Object>> resultado = new ArrayList<>();
            for (Map<?, ?> d : donacionesApi.obtenerTodosLosDonantes()) {
                boolean juridica = d.get("idJuridico") != null;
                Object id = juridica ? d.get("idJuridico") : d.get("idHumano");
                if (id == null) continue;
                String nombre = juridica
                        ? texto(d.get("razonSocial"))
                        : (texto(d.get("nombre")) + " " + texto(d.get("apellido"))).trim();
                if (!nombre.toLowerCase().contains(query)) continue;
                resultado.add(Map.of("id", id, "tipo", juridica ? "juridica" : "humana", "nombre", nombre));
                if (resultado.size() == 10) break;
            }
            return ResponseEntity.ok(resultado);
        } catch (SesionExpiradaException e) {
            return error(401, "Tu sesión expiró.");
        } catch (Exception e) {
            log.warn("No se pudo buscar donantes", e);
            return error(502, "No se pudo consultar a los donantes.");
        }
    }

    private static String texto(Object o) {
        return o != null ? String.valueOf(o) : "";
    }

    private static ResponseEntity<Map<String, Object>> error(int status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of("error", mensaje != null ? mensaje : "Error inesperado"));
    }
}