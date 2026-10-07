package ar.edu.utn.frba.ddsi.controllers;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/entidadBeneficiaria")
public class EntidadController {


    @GetMapping("/estadoDeDonaciones")
    public String estadoDeDonaciones(Model model) {
        model.addAttribute("titulo", "Estado de donaciones");
        model.addAttribute("tabActiva", "estado");
        return "acceso/entidadBeneficiaria/estadoDeDonaciones";
    }


    @GetMapping("/recepcionDeDonaciones")
    public String recepcionDeDonaciones(Model model) {
        model.addAttribute("titulo", "Recepcion de donaciones");
        model.addAttribute("tabActiva", "recepcion");
        return "acceso/entidadBeneficiaria/recepcionDeDonaciones";
    }

    @GetMapping("/registrarNecesidades")
    public String registrarNecesidades(Model model) {
        model.addAttribute("titulo", "Registrar necesidades");
        model.addAttribute("tabActiva", "registrar");
        return "acceso/entidadBeneficiaria/registrarNecesidades";
    }


    @GetMapping("/seguimientoDeEntrega")
    public String seguimientoDeEntrega(Model model) {
        model.addAttribute("titulo", "Seguimiento de entrega");
        model.addAttribute("tabActiva", "seguimiento");
        return "acceso/entidadBeneficiaria/seguimientoDeEntrega";
    }

    @GetMapping("/notificaciones")
    public String notificaciones(Model model) {
        model.addAttribute("titulo", "Notificaciones");
        model.addAttribute("tabActiva", "notificaciones");
        return "acceso/entidadBeneficiaria/notificaciones";
    }

    @GetMapping("/perfil")
    public String pefil(Model model) {
        model.addAttribute("titulo", "Mi perfil");
        model.addAttribute("tabActiva", " ");
        return "acceso/entidadBeneficiaria/perfil";
    }



}
