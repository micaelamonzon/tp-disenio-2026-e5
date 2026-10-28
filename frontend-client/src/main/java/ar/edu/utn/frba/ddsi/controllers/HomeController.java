package ar.edu.utn.frba.ddsi.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController implements ErrorController {
    @GetMapping("/")
    public String home() {
        // La landing es pública; las pantallas de cada rol piden iniciar sesión
        return "redirect:/acceso/landing";
    }

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("titulo", "No encontrado");
        return "error/404";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("titulo", "Acceso denegado");
        model.addAttribute("mensaje", "No tiene permiso para acceder a este recurso.");
        return "error/403";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return notFound(model);
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return accessDenied(model);
            }
        }

        // Por defecto si ocurre otro error
        model.addAttribute("titulo", "Error en el servidor");
        model.addAttribute("mensaje", "Ha ocurrido un error inesperado.");
        return "error/404";
    }
}

