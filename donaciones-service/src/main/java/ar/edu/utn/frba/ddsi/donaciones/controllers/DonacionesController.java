package ar.edu.utn.frba.ddsi.donaciones.controllers;
import ar.edu.utn.frba.ddsi.donaciones.dto.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.PropuestaMatch;
import ar.edu.utn.frba.ddsi.donaciones.services.DonacionesService;
import ar.edu.utn.frba.ddsi.donaciones.services.MatchmakingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/servicioDeDonaciones")
public class DonacionesController {

    private final DonacionesService donacionesService;
    private final MatchmakingService matchmakingService;

    public DonacionesController(DonacionesService donacionesService,
                                MatchmakingService matchmakingService) {
        this.donacionesService = donacionesService;
        this.matchmakingService = matchmakingService;
    }

    //Generales

    @GetMapping("/saludar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String primerEndPoint() {
        return "Hola desde el servicio de Donaciones!!";
    }

    @GetMapping("/obtenerDonantes")
    public ResponseEntity<List<PersonaDonanteDTO>> obtenerTodosLosDonantesUnificados() {
        return ResponseEntity.ok(donacionesService.obtenerTodosLosDonantesUnificados());
    }
    // Matching

    @GetMapping("/matcheos/{matcheoId}")
    public PropuestaMatch obtenerRankingGenerado(
            @PathVariable Long matcheoId) {
        return matchmakingService.obtenerPropuestaPorId(matcheoId);
    }

    @PostMapping("/matcheos/ejecutar")
    @ResponseStatus(HttpStatus.OK)
    public String ejecutarAlgoritmosADemanda() {
        matchmakingService.ejecutarProcesoMatchmaking();
        return "Proceso de asignación ejecutado a demanda exitosamente.";
    }

    @PostMapping("/matcheos/{matcheoId}/seleccion")
    public PropuestaMatch seleccionarNecesidad(
            @PathVariable Long matcheoId,
            @RequestBody SeleccionNecesidadRequestDTO request) {
        return matchmakingService.seleccionarNecesidad(
                matcheoId, request.necesidadId());
    }

    // Donantes
    @GetMapping("/obtenerDonantesJuridicos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaDonanteDTO> obtenerDonantesJuridicos() {
        List<PersonaDonanteDTO> donantesDTOS = this.donacionesService.obtenerTodosJuridicos();
        return donantesDTOS;
    }

    @GetMapping("/obtenerDonantesHumanos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaDonanteDTO> obtenerDonantesHumanos() {
        List<PersonaDonanteDTO> donantesDTOS = this.donacionesService.obtenerTodosHumanos();
        return donantesDTOS;
    }

    @PostMapping("/crearDonanteHumano")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaHumanaDTO crearDonanteHumano(@RequestBody PersonaHumanaDTO request) {
        PersonaHumanaDTO personaHumanaDTO = this.donacionesService.crearDonanteHumano(request);
        return personaHumanaDTO;
    }

    @PostMapping("/crearDonanteJuridico")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaJuridicaDTO crearDonanteJuridico(@RequestBody PersonaJuridicaDTO request) {
        PersonaJuridicaDTO personaJuridicaDTO = this.donacionesService.crearDonanteJuridico(request);
        return personaJuridicaDTO;
    }

    @PutMapping("/modificarDonanteHumano/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaHumanaDTO modificarDonanteHumano(
            @PathVariable Long id,
            @RequestBody PersonaHumanaDTO request) {
        PersonaHumanaDTO personaHumanaDTO = this.donacionesService.modificarDonanteHumano(id, request);
        return personaHumanaDTO;
    }

    @PutMapping("/modificarDonanteJuridico/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaJuridicaDTO modificarDonanteJuridico(
            @PathVariable Long id,
            @RequestBody PersonaJuridicaDTO request) {
        PersonaJuridicaDTO personaJuridicaDTO = this.donacionesService.modificarDonanteJuridico(id, request);
        return personaJuridicaDTO;
    }

    @DeleteMapping("/eliminarDonanteHumano/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void eliminarDonanteHumano(@PathVariable Long id) {
        this.donacionesService.eliminarDonanteHumano(id);
    }

    @DeleteMapping("/eliminarDonanteJuridico/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void eliminarDonanteJuridico(@PathVariable Long id) {
        this.donacionesService.eliminarDonanteJuridico(id);
    }
    // Donaciones

    @GetMapping("/humano/obtenerDonaciones/{id}")
    public ResponseEntity<PersonaDonanteDTO> obtenerDonacionesDeHumano(
            @PathVariable Long id) {
        try {
            PersonaDonanteDTO personaDonanteDTO = this.donacionesService.obtenerDonacionesDeHumano(id);
            return ResponseEntity.ok(personaDonanteDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/humana/CrearDonacion/{id}")
    public ResponseEntity<DonacionSinSegmentarDTO> crearDonacionDeHumano(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long id) {
        try {
            DonacionSinSegmentarDTO donacionSinSegmentarDTO = this.donacionesService.crearDonacionDeHumano(request, id);
            return ResponseEntity.ok(donacionSinSegmentarDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/humana/ModificarDonacion/{idHumano}/{idDonacion}/{idBien}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> modificarDonacionDeHumano(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long idHumano,
            @PathVariable Long idDonacion,
            @PathVariable Long idBien) {
        try {
            List<DonacionSinSegmentarDTO> donacionesSinSegmentarDTO = this.donacionesService.modificarDonacionDeHumano(request, idHumano, idDonacion, idBien);
            return ResponseEntity.ok(donacionesSinSegmentarDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/humana/EliminarDonacion/{idHumano}/{idDonacion}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> eliminarDonacionDeHumano(
            @PathVariable Long idHumano,
            @PathVariable Long idDonacion) {
        try {
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTO = this.donacionesService.eliminarDonacionDeHumano(idHumano, idDonacion);
            return donacionSinSegmentarDTO == null ?
                    ResponseEntity.notFound().build() :
                    ResponseEntity.ok(donacionSinSegmentarDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/juridica/obtenerDonaciones/{id}")
    public ResponseEntity<PersonaDonanteDTO> obtenerDonacionesDeJuridico(
            @PathVariable Long id) {
        try {
            PersonaDonanteDTO personaDonanteDTO = this.donacionesService.obtenerDonacionesDeJuridico(id);
            return ResponseEntity.ok(personaDonanteDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/juridica/CrearDonacion/{id}")
    public ResponseEntity<DonacionSinSegmentarDTO> crearDonacionDeJuridico(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long id) {
        try {
            DonacionSinSegmentarDTO donacionSinSegmentarDTO = this.donacionesService.crearDonacionDeJuridico(request, id);
            return ResponseEntity.ok(donacionSinSegmentarDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/juridico/ModificarDonacion/{idJuridico}/{idDonacion}/{idBien}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> modificarDonacionDeJuridico(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long idJuridico,
            @PathVariable Long idDonacion,
            @PathVariable Long idBien) {
        try {
            List<DonacionSinSegmentarDTO> donacionesSinSegmentarDTO = this.donacionesService.modificarDonacionDeJuridica(request, idJuridico, idDonacion, idBien);
            return ResponseEntity.ok(donacionesSinSegmentarDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/juridico/EliminarDonacion/{idJuridico}/{idDonacion}")
    public ResponseEntity<List<DonacionSinSegmentarDTO>> eliminarDonacionDeJuridico(
            @PathVariable Long idJuridico,
            @PathVariable Long idDonacion) {
        try {
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTO = this.donacionesService.eliminarDonacionDeJuridico(idJuridico, idDonacion);
            return donacionSinSegmentarDTO == null ?
                    ResponseEntity.notFound().build() :
                    ResponseEntity.ok(donacionSinSegmentarDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
