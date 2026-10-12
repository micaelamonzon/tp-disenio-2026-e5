package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.LogisticaApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/personaAdministradora")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final IncentivosService incentivosService;
    private final LogisticaApiService logisticaApiService;

    public AdminController(IncentivosService incentivosService, LogisticaApiService logisticaApiService) {
        this.incentivosService = incentivosService;
        this.logisticaApiService = logisticaApiService;
    }


    @GetMapping("/ranking")
    public String verRankingAdmin(@RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer anio, Model model) {
        RankingMensualDTO rankingActual = incentivosService.obtenerUltimoRanking();
        List<RankingMensualDTO> historialRankings = incentivosService.buscarRankings(mes, anio);

        model.addAttribute("rankingActual", rankingActual);
        model.addAttribute("historialRankings", historialRankings);

        return "acceso/personaAdministradora/ranking";
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
        return "acceso/personaAdministradora/home";
    }
}