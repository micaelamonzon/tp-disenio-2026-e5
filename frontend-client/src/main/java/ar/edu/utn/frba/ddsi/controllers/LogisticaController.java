package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.EntregaDTO;
import ar.edu.utn.frba.ddsi.services.LogisticaApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/logistica")
public class LogisticaController {

    // Vuelve al panel de administración, directo a la vista de camiones.
    private static final String VOLVER_A_CAMIONES = "redirect:/personaAdministradora/home#camiones";

    private final LogisticaApiService logisticaApiService;

    public LogisticaController(LogisticaApiService logisticaApiService) {
        this.logisticaApiService = logisticaApiService;
    }

    @GetMapping("/camiones")
    public String camiones(Model model) {
        List<CamionDTO> camiones = logisticaApiService.obtenerCamiones();
        model.addAttribute("camiones", camiones);
        return "acceso/personaAdministradora/logistica/camiones"; // ← cambiado
    }

    // Formulario "Nuevo Camión" del home: los campos del form (patente, volumen, altura,
    // capacidadCarga) se bindean directo al CamionDTO.
    @PostMapping("/camiones")
    public String crearCamion(@ModelAttribute CamionDTO camion, RedirectAttributes redirectAttributes) {
        camion.setDisponible(true);
        try {
            logisticaApiService.crearCamion(camion);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCamiones", "No se pudo crear el camión.");
        }
        return VOLVER_A_CAMIONES;
    }

    @PostMapping("/camiones/{id}/eliminar")
    public String eliminarCamion(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            logisticaApiService.eliminarCamion(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCamiones", "No se pudo eliminar el camión.");
        }
        return VOLVER_A_CAMIONES;
    }

    @GetMapping("/entregas")
    public String entregas(Model model) {
        List<EntregaDTO> entregas = logisticaApiService.obtenerEntregas();
        model.addAttribute("entregas", entregas);
        return "acceso/personaAdministradora/logistica/entregas"; // ← cambiado
    }

    @GetMapping("/entregas/{id}")
    public String detalleEntrega(@PathVariable String id, Model model) {
        EntregaDTO entrega = logisticaApiService.obtenerEntregaPorId(id);
        model.addAttribute("entrega", entrega);
        return "acceso/personaAdministradora/logistica/detalle-entrega"; // ← cambiado
    }
}