package ar.edu.utn.frba.ddsi.notificaciones.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicioDeNotificaciones") //llamar al endpoint asi: localhost:8081/servicioDeNotificaciones/notificar
public class controllerDeNotificaciones {

    @GetMapping("/notificar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String segundoEndpoint() {
        return "Ok, tu notificacion se envio y se completo";
    }


}
