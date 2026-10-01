package ar.edu.utn.frba.ddsi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login") // CAMBIAR DIRECCION LOGIN
    public String login() {
        return "login";
    }
}
