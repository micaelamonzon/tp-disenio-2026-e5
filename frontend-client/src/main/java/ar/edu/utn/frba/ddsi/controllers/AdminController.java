package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/personaAdministradora")
public class AdminController {
    private final IncentivosService incentivosService;

    public AdminController(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
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
        return "acceso/personaAdministradora/home";
    }
}