package ar.edu.utn.frba.ddsi.donaciones.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicioDeDonaciones")   //llamar desde postman o google como -> localhost:8080/servicioDeDonaciones/saludar
public class controllerDonaciones {

    @GetMapping("/saludar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String primerEndPoint(){
        return "Hola desde el servicio de Donaciones!!";
    }

}

