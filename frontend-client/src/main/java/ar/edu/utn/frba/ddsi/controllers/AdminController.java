package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/personaAdministradora")
public class AdminController {
    private final IncentivosService incentivosService;

    public AdminController(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
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

    @GetMapping("/home")
    public String panelAdministrador(Model model) {
        model.addAttribute("titulo", "DonaTrack - Panel de Administración");
        return "acceso/personaAdministradora/home";
    }

    @GetMapping("/index")
    public String index() {
        return "acceso/personaAdministradora/index";
    }
}