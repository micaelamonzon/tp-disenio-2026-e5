package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.logistica.CamionDTO;
import ar.edu.utn.frba.ddsi.dtos.logistica.EntregaDTO;
import ar.edu.utn.frba.ddsi.services.LogisticaApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/logistica")
public class LogisticaController {

    private final LogisticaApiService logisticaApiService;

    public LogisticaController(LogisticaApiService logisticaApiService) {
        this.logisticaApiService = logisticaApiService;
    }

    @GetMapping("/camiones")
    public String camiones(Model model) {
        List<CamionDTO> camiones = logisticaApiService.obtenerCamiones();
        model.addAttribute("camiones", camiones);
        return "logistica/camiones";
    }

    @GetMapping("/entregas")
    public String entregas(Model model) {
        List<EntregaDTO> entregas = logisticaApiService.obtenerEntregas();
        model.addAttribute("entregas", entregas);
        return "logistica/entregas";
    }

    @GetMapping("/entregas/{id}")
    public String detalleEntrega(@PathVariable String id, Model model) {
        EntregaDTO entrega = logisticaApiService.obtenerEntregaPorId(id);
        model.addAttribute("entrega", entrega);
        return "logistica/detalle-entrega";
    }
}