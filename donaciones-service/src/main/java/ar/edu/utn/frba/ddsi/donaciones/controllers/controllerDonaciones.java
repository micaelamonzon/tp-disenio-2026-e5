package ar.edu.utn.frba.ddsi.donaciones.controllers;


import ar.edu.utn.frba.ddsi.donaciones.dto.SeleccionNecesidadRequestDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import ar.edu.utn.frba.ddsi.donaciones.services.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/servicioDeDonaciones")   //llamar desde postman o google como -> localhost:8080/servicioDeDonaciones/saludar
public class controllerDonaciones {
    private final MatchmakingService matchmakingService;

    @GetMapping("/saludar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String primerEndPoint(){
        return "Hola desde el servicio de Donaciones!!";
    }

    @PostMapping("/matcheos/{matcheoId}/seleccion")
    public PropuestaMatch seleccionarNecesidad(
            @PathVariable Long matcheoId,
            @RequestBody SeleccionNecesidadRequestDTO request
    ) {
        return matchmakingService.seleccionarNecesidad(matcheoId, request.necesidadId());
    }

}

