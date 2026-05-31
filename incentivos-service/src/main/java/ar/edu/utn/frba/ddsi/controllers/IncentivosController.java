package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.dto.InsigniaDTO;
import ar.edu.utn.frba.ddsi.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.impl.IncentivosServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/incentivos-service")
public class IncentivosController {
    private final IncentivosService incentivosService;


    public IncentivosController(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
    }

    //Obtención de las misiones completadas por una persona donante.
    @GetMapping("/misionesCompletadas/{id}/")
    @ResponseStatus(HttpStatus.CREATED) //para devolver el 200
    public List<MisionDTO> obtenerTodasLasMisiones(@PathVariable Long id){

        return incentivosService.buscarMisionesCompletadas(id);
    }
    //Obtención de las insignias para una persona donante.
    @GetMapping("{id}/insignias")
    public List<InsigniaDTO> obtenerInsignias(@PathVariable Long id){
        return incentivosService.buscarInsigniasPorId(id);
    }
    @GetMapping("{id}/misionActual")
    public MisionDTO obtenerMisionEnCurso(@PathVariable Long id){
        return incentivosService.buscarMisionActualPorId(id);
    }

    @GetMapping("/verificarMision/{idPersona}")
    public ResponseEntity<Void> verificarMision(@PathVariable Long id){
        Insignia insigniaObtenida = Insignia.COLABORADOR; // ejemplo

        incentivosService.publicarYDifundirInsignia(id, insigniaObtenida);

        return ResponseEntity.ok().build();
    }
}
