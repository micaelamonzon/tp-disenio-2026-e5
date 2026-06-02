package ar.edu.utn.frba.ddsi.donaciones.controllers;
import ar.edu.utn.frba.ddsi.donaciones.dto.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import ar.edu.utn.frba.ddsi.donaciones.services.DonacionesService;
import ar.edu.utn.frba.ddsi.donaciones.services.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")   //llamar desde postman o google como -> localhost:8080/servicioDeDonaciones/saludar
public class DonacionesController {
    private final DonacionesService donacionesService;

    public DonacionesController(DonacionesService donacionesService, MatchmakingService matchmakingService) {
        this.donacionesService = donacionesService;
        this.matchmakingService = matchmakingService;
    }

    private final MatchmakingService matchmakingService;

    @GetMapping("/saludar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String primerEndPoint(){
        return "Hola desde el servicio de Donaciones!!";
    }

    @GetMapping("/obtenerDonantesHumanos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaDonanteDTO> obtenerDonantesHumanos(){
        List<PersonaDonanteDTO> donantesDTOS = this.donacionesService.obtenerTodosHumanos();
        return donantesDTOS;
    }

    @GetMapping("/obtenerDonantesJuridicos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaDonanteDTO> obtenerDonantesJuridicos(){
        List<PersonaDonanteDTO> donantesDTOS = this.donacionesService.obtenerTodosJuridicos();
        return donantesDTOS;
    }

    @GetMapping("/humano/obtenerDonaciones/{id}")
    public ResponseEntity<PersonaDonanteDTO> obtenerDonacionesDeHumano(@PathVariable Long id){
       try{
           PersonaDonanteDTO personaDonanteDTO = this.donacionesService.obtenerDonacionesDeHumano(id);
           return ResponseEntity.ok(personaDonanteDTO);
       }catch (RuntimeException e){
           System.out.println(e.getMessage());
           return ResponseEntity.notFound().build();
       }
    }
    
    @GetMapping("/juridica/obtenerDonaciones/{id}")
    public ResponseEntity<PersonaDonanteDTO> obtenerDonacionesDeJuridico(@PathVariable Long id){
        try{
            PersonaDonanteDTO personaDonanteDTO = this.donacionesService.obtenerDonacionesDeJurico(id);
            return ResponseEntity.ok(personaDonanteDTO);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/juridica/CrearDonacion/{id}")
    public ResponseEntity<DonacionSinSegmentarDTO> CrearDonacionDeJuridico(@RequestBody DonacionSinSegmentarDTO request, @PathVariable Long id){
        try{
           DonacionSinSegmentarDTO donacionSinSegmentarDTO= this.donacionesService.crearDonacionDeJuridico(request, id);
            return ResponseEntity.ok(donacionSinSegmentarDTO);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/humana/CrearDonacion/{id}")
    public ResponseEntity<DonacionSinSegmentarDTO> CrearDonacionDeHumano(@RequestBody DonacionSinSegmentarDTO request, @PathVariable Long id){
        try{
            DonacionSinSegmentarDTO donacionSinSegmentarDTO= this.donacionesService.crearDonacionDeHumano(request, id);
            return ResponseEntity.ok(donacionSinSegmentarDTO);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/humana/ModificarDonacion/{idHumano}/{idDonacion}/{idBien}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> ModificarDonacionDeHumano(@RequestBody DonacionSinSegmentarDTO request, @PathVariable Long idHumano, @PathVariable Long idDonacion,  @PathVariable Long idBien){
        try{
            List<DonacionSinSegmentarDTO> donacionesSinSegmentarDTO= this.donacionesService.modificarDonacionDeHumano(request, idHumano, idDonacion, idBien);
            return ResponseEntity.ok(donacionesSinSegmentarDTO);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/juridico/ModificarDonacion/{idJuridico}/{idDonacion}/{idBien}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> ModificarDonacionDeJuridico(@RequestBody DonacionSinSegmentarDTO request, @PathVariable Long idJuridico, @PathVariable Long idDonacion,  @PathVariable Long idBien){
        try{
            List<DonacionSinSegmentarDTO> donacionesSinSegmentarDTO= this.donacionesService.modificarDonacionDeJuridica(request, idJuridico, idDonacion, idBien);
            return ResponseEntity.ok(donacionesSinSegmentarDTO);
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/humana/EliminarDonacion/{idHumano}/{idDonacion}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> EliminarDonacionDeHumano(@PathVariable Long idHumano, @PathVariable Long idDonacion){
        try{
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTO= this.donacionesService.eliminarDonacionDeHumano(idHumano, idDonacion);
            return  donacionSinSegmentarDTO == null ?
                    ResponseEntity.notFound().build() :
                    ResponseEntity.ok(donacionSinSegmentarDTO);

        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/juridico/EliminarDonacion/{idHumano}/{idDonacion}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> EliminarDonacionDeJuridico(@PathVariable Long idJuridico, @PathVariable Long idDonacion){
        try{
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTO= this.donacionesService.eliminarDonacionDeJuridico(idJuridico, idDonacion);
            return  donacionSinSegmentarDTO == null ?
                    ResponseEntity.notFound().build() :
                    ResponseEntity.ok(donacionSinSegmentarDTO);

        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crearDonanteHumano")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaHumanaDTO crearDonanteHumanos(@RequestBody PersonaHumanaDTO request){
        PersonaHumanaDTO personaHumanaDTO = this.donacionesService.crearDonanteHumano(request);
        return personaHumanaDTO;
    }

    @GetMapping("/matcheos/{matcheoId}")
    public PropuestaMatch obtenerRankingGenerado(@PathVariable Long matcheoId) {
        return matchmakingService.obtenerPropuestaPorId(matcheoId);
    }

    @PostMapping("/matcheos/ejecutar")
    @ResponseStatus(HttpStatus.OK)
    public String ejecutarAlgoritmosADemanda() {
        matchmakingService.ejecutarProcesoMatchmaking(); // <-- Reutilización
        return "Proceso de asignación ejecutado a demanda exitosamente.";
    }

    @PostMapping("/matcheos/{matcheoId}/seleccion")
    public PropuestaMatch seleccionarNecesidad(
            @PathVariable Long matcheoId,
            @RequestBody SeleccionNecesidadRequestDTO request
    ) {
        return matchmakingService.seleccionarNecesidad(matcheoId, request.necesidadId());
    }


}

