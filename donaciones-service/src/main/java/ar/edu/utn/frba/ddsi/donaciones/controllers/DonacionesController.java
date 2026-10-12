package ar.edu.utn.frba.ddsi.donaciones.controllers;
import ar.edu.utn.frba.ddsi.donaciones.dto.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos.PropuestaMatch;
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

    @GetMapping("/donantes")
    public ResponseEntity<List<PersonaDonanteDTO>> obtenerTodosLosDonantesUnificados() {
        return ResponseEntity.ok(donacionesService.obtenerTodosLosDonantesUnificados());
    }
    /*ESTE ENDPOINT ES PROVISORIO, MÁS ADELANTE SERÁ REEMPLAZADO PARA QUE ADMITA UN ARCHIVO POSTA, EN VEZ DE LA RUTA*/
    @PostMapping("/importadorCSV") //FUNCIONA -> TESTEE CON POSTMAN
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void importadorCSV(@RequestParam("file") String file) {
        donacionesService.arrancarImportacionDeCSV(file);
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
    @GetMapping("donantes/juridicos") //FUNCIONA -> TESTEE CON POSTMAN
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaJuridicaDTO> obtenerDonantesJuridicos() {
        List<PersonaJuridicaDTO> donantesDTOS = this.donacionesService.obtenerTodosJuridicos();
        return donantesDTOS;
    }

    @GetMapping("donantes/humanos") //FUNCIONA -> TESTEE CON POSTMAN
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<PersonaHumanaDTO> obtenerDonantesHumanos() {
        List<PersonaHumanaDTO> donantesDTOS = this.donacionesService.obtenerTodosHumanos();
        return donantesDTOS;
    }

    @PostMapping("donante/humano")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaHumanaDTO crearDonanteHumano(@RequestBody PersonaHumanaDTO request) {
        PersonaHumanaDTO personaHumanaDTO = this.donacionesService.crearDonanteHumano(request);
        return personaHumanaDTO;
    }

    @PostMapping("/donante/juridico")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaJuridicaDTO crearDonanteJuridico(@RequestBody PersonaJuridicaDTO request) {
        PersonaJuridicaDTO personaJuridicaDTO = this.donacionesService.crearDonanteJuridico(request);
        return personaJuridicaDTO;
    }

    @PutMapping("/donante/humano/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaHumanaDTO modificarDonanteHumano(
            @PathVariable Long id,
            @RequestBody PersonaHumanaDTO request) {
        PersonaHumanaDTO personaHumanaDTO = this.donacionesService.modificarDonanteHumano(id, request);
        return personaHumanaDTO;
    }

    @PutMapping("/donante/juridico/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PersonaJuridicaDTO modificarDonanteJuridico(
            @PathVariable Long id,
            @RequestBody PersonaJuridicaDTO request) {
        PersonaJuridicaDTO personaJuridicaDTO = this.donacionesService.modificarDonanteJuridico(id, request);
        return personaJuridicaDTO;
    }

    @DeleteMapping("/donante/humano/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void eliminarDonanteHumano(@PathVariable Long id) {
        this.donacionesService.eliminarDonanteHumano(id);
    }

    @DeleteMapping("/donante/juridico/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void eliminarDonanteJuridico(@PathVariable Long id) {
        this.donacionesService.eliminarDonanteJuridico(id);
    }

    @GetMapping("donante/humano/{id}") //FUNICONA -> TESTEE CON POSTMAN
    public ResponseEntity<PersonaHumanaDTO> obtenerHumanoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(this.donacionesService.obtenerHumanoPorId(id));
    }

    @GetMapping("donante/juridico/{id}") //FUNICONA -> TESTEE CON POSTMAN
    public ResponseEntity<PersonaJuridicaDTO> obtenerJuridicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(this.donacionesService.obtenerJuridicoPorId(id));
    }

    // Donaciones
    @GetMapping("donaciones/humano/{id}")//FUNCIONA -> TESTEE CON POSTMAN
    public ResponseEntity<List<DonacionSegmentadaDTO>> obtenerDonacionesDeHumano(
            @PathVariable Long id) {
        try {
            List<DonacionSegmentadaDTO> donaciones= this.donacionesService.obtenerDonacionesDeHumano(id);
            return ResponseEntity.ok(donaciones);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/humano/donacion/{id}")
    public ResponseEntity<List<DonacionSegmentadaDTO>> crearDonacionDeHumano(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long id) {
        try {
            List<DonacionSegmentadaDTO> donacionesSegmentadasDTO = this.donacionesService.crearDonacionDeHumano(request, id);
            return ResponseEntity.ok(donacionesSegmentadasDTO);
        } catch (RuntimeException e) {
            e.printStackTrace(); // Muestra la clase y la línea exacta del fallo
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/humano/donacion/{idHumano}/{idDonacion}/{idBien}")
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

    @DeleteMapping("/humano/donacion/{idHumano}/{idDonacion}")
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


    @GetMapping("donaciones/juridico/{id}") //FUNCIONA -> TESTEE CON POSTMAN
    public ResponseEntity<List<DonacionSegmentadaDTO>> obtenerDonacionesDeJuridico(
            @PathVariable Long id) {
        try {
            List<DonacionSegmentadaDTO> donacionesDTO = this.donacionesService.obtenerDonacionesDeJuridico(id);
            return ResponseEntity.ok(donacionesDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/juridico/donacion/{id}") //FUNCIONA -> TESTEE CON POSTMAN
    public ResponseEntity<List<DonacionSegmentadaDTO>> crearDonacionDeJuridico(
            @RequestBody DonacionSinSegmentarDTO request,
            @PathVariable Long id) {
        try {
            List<DonacionSegmentadaDTO> donacionesSegmentadasDTO = this.donacionesService.crearDonacionDeJuridico(request, id);
            return ResponseEntity.ok(donacionesSegmentadasDTO);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/juridico/donacion/{idJuridico}/{idDonacion}/{idBien}")
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

    @DeleteMapping("/juridico/donacion/{idJuridico}/{idDonacion}")
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
