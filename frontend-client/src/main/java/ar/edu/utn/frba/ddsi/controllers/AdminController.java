package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.services.DonacionesService;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.LogisticaApiService;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import ar.edu.utn.frba.ddsi.utils.UsuarioActual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/personaAdministradora")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final IncentivosService incentivosService;
    private final LogisticaApiService logisticaApiService;
    private final DonacionesService donacionesService;
    private final UsuarioActual usuarioActual;

    public AdminController(IncentivosService incentivosService,
                           LogisticaApiService logisticaApiService,
                           DonacionesService donacionesService,
                           UsuarioActual usuarioActual) {
        this.incentivosService = incentivosService;
        this.logisticaApiService = logisticaApiService;
        this.donacionesService = donacionesService;
        this.usuarioActual = usuarioActual;
    }


    @GetMapping("/ranking")
    public String verRankingAdmin(@RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer anio, Model model) {
        RankingMensualDTO rankingActual = incentivosService.obtenerUltimoRanking();
        List<RankingMensualDTO> historialRankings = incentivosService.buscarRankings(mes, anio);

        model.addAttribute("rankingActual", rankingActual);
        model.addAttribute("historialRankings", historialRankings);

        return "acceso/personaAdministradora/ranking";
    }

    // Notificaciones de la persona administradora. Por ahora no hay una fuente de datos para el admin
    // (se va a definir con el cron-job): la vista queda lista y muestra "Todavía no tenés notificaciones".
    // Cuando exista el servicio, cargar acá "notificaciones" (cada una con mensaje, medio y fechaEnvio).
    @GetMapping("/notificaciones")
    public String notificaciones(Model model) {
        model.addAttribute("notificaciones", java.util.List.of());
        return "acceso/personaAdministradora/notificaciones";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/personaAdministradora/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("titulo", "DonaTrack - Panel de Administración");

        // La vista "Gestión de Camiones" se renderiza acá con Thymeleaf. Si logística
        // no responde, la página igual carga (con la lista vacía y un aviso).
        try {
            model.addAttribute("camiones", logisticaApiService.obtenerCamiones());
        } catch (Exception e) {
            log.warn("No se pudieron obtener los camiones de logística", e);
            model.addAttribute("camiones", List.<CamionDTO>of());
            model.addAttribute("errorCamiones", "No se pudo conectar con logística.");
        }

        // Datos para la vista "Ranking de Donantes", que reutiliza ranking.html de la compañera.
        try {
            model.addAttribute("rankingActual", incentivosService.obtenerUltimoRanking());
            model.addAttribute("historialRankings", incentivosService.buscarRankings(null, null));
        } catch (Exception e) {
            log.warn("No se pudo obtener el ranking de incentivos", e);
            model.addAttribute("historialRankings", java.util.List.<RankingMensualDTO>of());
        }

        try {
            List<DonacionDTO> inventario = new ArrayList<>();
            inventario.addAll(donacionesService.obtenerDonacionesPorEstado("EN_DEPOSITO"));
            inventario.addAll(donacionesService.obtenerDonacionesPorEstado("VENCIDA"));
            model.addAttribute("inventario", inventario);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo obtener el inventario", e);
            model.addAttribute("inventario", List.<DonacionDTO>of());
            model.addAttribute("errorInventario", "No se pudo conectar con el servicio de donaciones.");
        }
        return "acceso/personaAdministradora/home";
    }

    @PostMapping("/inventario/{id}/vencer")
    public String vencer(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            donacionesService.vencerDonacion(id, usuarioActual.obtener().email());
            redirect.addFlashAttribute("mensajeInventario", "Donación #" + id + " marcada como vencida.");
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.warn("No se pudo vencer la donación {}", id, e);
            redirect.addFlashAttribute("errorInventario", "No se pudo marcar la donación como vencida.");
        }
        return "redirect:/personaAdministradora/home#inventario";
    }
}