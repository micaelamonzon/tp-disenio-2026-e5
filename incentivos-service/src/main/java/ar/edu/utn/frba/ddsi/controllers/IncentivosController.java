package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/incentivos-service")
public class IncentivosController {
    private final IncentivosService incentivosService;


    public IncentivosController(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
    }

    //Obtención de las misiones completadas por una persona donante.
    @GetMapping("donanteHumano/misionesCompletadas/{id}/")
    @ResponseStatus(HttpStatus.ACCEPTED) //para devolver el 200
    public List<MisionDTO> obtenerTodasLasMisionesDeDonanteHumano(@PathVariable Long id){
        return incentivosService.obtenerDonanteHumano(id);
    }

    @GetMapping("donanteJuridico/misionesCompletadas/{id}/")
    @ResponseStatus(HttpStatus.ACCEPTED) //para devolver el 200
    public List<MisionDTO> obtenerTodasLasMisionesDeDonanteJuridico(@PathVariable Long id){
        return incentivosService.obtenerDonanteJuridico(id);
    }
    //Obtención de las insignias para una persona donante.
    @GetMapping("{id}/insignias")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<InsigniaDTO> obtenerInsignias(@PathVariable Long id){
        try{
            return incentivosService.buscarInsigniasPorId(id);
        }catch(RuntimeException e){
            throw new RuntimeException("Error al obtener las insignias porque " + e.getMessage());
        }

    }
    @GetMapping("{id}/misionActual")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MisionDTO obtenerMisionEnCurso(@PathVariable Long id){
        try{
            return incentivosService.buscarMisionActualPorId(id);
        }catch(RuntimeException e){
            throw new RuntimeException("Error al obtener la mision actual porque " + e.getMessage());
        }
    }

    @GetMapping("/verificarMision/{idPersona}")
    public ResponseEntity<Void> verificarMision(@PathVariable Long id){
        Insignia insigniaObtenida = Insignia.COLABORADOR; // ejemplo

        incentivosService.publicarYDifundirInsignia(id, insigniaObtenida);

        return ResponseEntity.ok().build();
    }
}
