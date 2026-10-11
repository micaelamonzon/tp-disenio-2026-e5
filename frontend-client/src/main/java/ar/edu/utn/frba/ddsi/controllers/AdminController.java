package ar.edu.utn.frba.ddsi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/personaAdministradora")
public class AdminController {

    @GetMapping("/index")
    public String index() {
        return "acceso/personaAdministradora/index";
    }
}