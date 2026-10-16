package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dto.DonacionIncentivosDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.impl.ReceptorWebHookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("incentivos/webhooks")
public class WebHookController {
    private final ReceptorWebHookService webHookReceptorService;



    public WebHookController(ReceptorWebHookService webHookReceptorService) {
        this.webHookReceptorService = webHookReceptorService;
    }


    @PostMapping("/donante")
    public ResponseEntity<Void> nuevoDonante(@RequestBody PersonaDonanteDTO nuevoDonante) {
        webHookReceptorService.registrarDonante(nuevoDonante);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/donantes")
    public ResponseEntity<Void> nuevosDonantes(@RequestBody List<PersonaDonanteDTO> nuevosDonantes) {
        webHookReceptorService.registrarDonantes(nuevosDonantes);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/donaciones/lote/{tipoDonante}/{id}")
    public ResponseEntity<Void> nuevasDonaciones(@RequestBody List<DonacionIncentivosDTO> nuevasDonaciones, @PathVariable TipoDeDonante tipoDonante, @PathVariable Long id) {
        webHookReceptorService.registrarDonaciones(nuevasDonaciones, tipoDonante, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/donacion/{tipoDonante}/{id}")
    public ResponseEntity<Void> nuevaDonacion(@RequestBody DonacionIncentivosDTO donacion,
                                              @PathVariable TipoDeDonante tipoDonante,
                                              @PathVariable Long id) {
        webHookReceptorService.registrarDonacion(donacion, tipoDonante, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/donacion/{idDonacionEnDonaciones}/fueEntregada")
    public ResponseEntity<Void> donacionEntregada(@PathVariable Long idDonacionEnDonaciones) {
        webHookReceptorService.registrarDonacionEntregada(idDonacionEnDonaciones);
        return ResponseEntity.ok().build();
    }

}
