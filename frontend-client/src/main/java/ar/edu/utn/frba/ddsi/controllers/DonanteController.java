package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.services.DonacionesApiService.DonanteNoEncontradoException;
import ar.edu.utn.frba.ddsi.services.DonacionesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DonanteController {

    private final DonacionesService donacionesService;

    public DonanteController(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    //Despues el id debe salir del usuario logueado no de la URL
    @GetMapping("/donante/{idDonante}/donaciones")
    public String misDonaciones(@PathVariable Long idDonante, Model model) {
        model.addAttribute("idDonante", idDonante);
        try {
            model.addAttribute("donaciones", donacionesService.obtenerDonacionesDeHumano(idDonante));
        } catch (DonanteNoEncontradoException e) {
            model.addAttribute("donaciones", List.of());
            model.addAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("donaciones", List.of());
            model.addAttribute("error", "El servicio de donaciones no está disponible. Intentá de nuevo en unos minutos.");
        }
        return "Acceso/Donante/misDonaciones";
    }
}
