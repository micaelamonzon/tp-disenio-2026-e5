package ar.edu.utn.frba.ddsi.donaciones.controllers;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaJuridicaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.services.DonacionesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")   //llamar desde postman o google como -> localhost:8080/servicioDeDonaciones/saludar
public class DonacionesController {
    private final DonacionesService donacionesService;

    public DonacionesController(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    @GetMapping("/saludar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String primerEndPoint(){
        return "Hola desde el servicio de Donaciones!!";
    }

    @GetMapping("/obtenerDonantesHumanos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaHumanaDTO> obtenerDonantesHumanos(){
        List<PersonaHumanaDTO> donantesDTOS = this.donacionesService.obtenerTodosHumanos();
        return donantesDTOS;
    }

    @GetMapping("/obtenerDonantesJuridicos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaJuridicaDTO> obtenerDonantesJuridicos(){
        List<PersonaJuridicaDTO> donantesDTOS = this.donacionesService.obtenerTodosJuridicos();
        return donantesDTOS;
    }

    @GetMapping("/humano/obtenerDonaciones/{id}")

    public ResponseEntity<List<DonacionSinSegmentarDTO>> obtenerDonacionesDeHumano(@PathVariable Long id){
       try{
           List<DonacionSinSegmentarDTO> donaciones = this.donacionesService.obtenerDonacionesDeHumano(id);
           return ResponseEntity.ok(donaciones);
       }catch (RuntimeException e){
           System.out.println(e.getMessage());
           return ResponseEntity.notFound().build();
       }
    }

    @PostMapping("/humano")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaHumanaDTO crearDonanteHumanos(@RequestBody PersonaHumanaDTO request){
        PersonaHumanaDTO personaHumanaDTO = this.donacionesService.crearDonanteHumanos(request);
        return personaHumanaDTO;
    }

}

