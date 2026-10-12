package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.LogisticaApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/personaAdministradora")
public class AdminController {
    private final IncentivosService incentivosService;
    private final LogisticaApiService logisticaApiService;

    public AdminController(IncentivosService incentivosService,
                           LogisticaApiService logisticaApiService) {
        this.incentivosService = incentivosService;
        this.logisticaApiService = logisticaApiService;
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/personaAdministradora/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("titulo", "DonaTrack - Panel de Administración");
        try {
            model.addAttribute("camiones", logisticaApiService.obtenerCamiones());
        } catch (Exception e) {
            model.addAttribute("camiones", List.of());
        }
        return "acceso/personaAdministradora/home";
    }

    /*
    @GetMapping("/ranking")
    public String verRankingAdmin(@RequestParam(required = false) Integer mes, @RequestParam(required = false) Integer anio, Model model) {
        RankingMensualDTO rankingActual = incentivosService.obtenerUltimoRanking();
        List<RankingMensualDTO> historialRankings = incentivosService.buscarRankings(mes, anio);

        model.addAttribute("rankingActual", rankingActual);
        model.addAttribute("historialRankings", historialRankings);

        return "admin/personaAdmin";
    } todavia no esta probado*/
}