package ar.edu.utn.frba.ddsi.config;

import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Si en cualquier pantalla autenticación rechaza el refresh, se cierra
// la sesión del front y se vuelve al login con un aviso
@ControllerAdvice
public class SesionExpiradaHandler {

    @ExceptionHandler(SesionExpiradaException.class)
    public String sesionExpirada(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/acceso/login?expirada";
    }
}
