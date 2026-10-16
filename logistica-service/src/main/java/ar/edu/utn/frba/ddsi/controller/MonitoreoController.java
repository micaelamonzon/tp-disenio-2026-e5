package ar.edu.utn.frba.ddsi.controller;
import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.dto.RutaEntregaDTO;
import ar.edu.utn.frba.ddsi.models.entities.TipoDeDonante;
import ar.edu.utn.frba.ddsi.services.MonitoreoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logistica-service/camiones/monitoreo/ruta")
public class MonitoreoController {

    private final MonitoreoService monitoreoService;


    public MonitoreoController(MonitoreoService monitoreoService) {
        this.monitoreoService = monitoreoService;
    }


    // Rutas de las entregas que llevan donaciones del donante
    @GetMapping("donante/{idDonante}/recorrido")
    public ResponseEntity<List<RutaEntregaDTO>> obtenerRecorridoDeDonacionesDeDonante(@PathVariable Long idDonante, @RequestParam TipoDeDonante tipoDonante) {

        return ResponseEntity.ok(monitoreoService.obtenerRecorridoDeDonacionesDeDonante(idDonante, tipoDonante));
    }

    // Rutas de las entregas activas de la entidad, recortadas hasta su parada (req. 14)
    @GetMapping("entidad/{idEntidad}/recorrido")
    public ResponseEntity<List<RutaEntregaDTO>> obtenerRecorridoDeEntregasDeEntidad(@PathVariable Long idEntidad) {
        return ResponseEntity.ok(monitoreoService.obtenerRecorridoDeEntregasDeEntidad(idEntidad));
    }
}
