package ar.edu.utn.frba.ddsi.controller;
import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.services.MonitoreoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logistica-service/camiones/monitoreo/ruta")
public class MonitoreoController {

    private final MonitoreoService monitoreoService;


    public MonitoreoController(MonitoreoService monitoreoService) {
        this.monitoreoService = monitoreoService;

    }

    //Endpoint para registrar la posición de un camión en tiempo real, es lo que recibimos del GPS.
    @PostMapping("camion/{idCamion}/posicionActual")
    public ResponseEntity<PosicionDTO> obtenerPosicionActual(@PathVariable Long idCamion, @RequestBody PosicionDTO posicionDTO) {

        PosicionDTO posicionActual = monitoreoService.obtenerPosicionActualDeCamion(idCamion, posicionDTO);
        if (posicionActual == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(posicionActual);
    }

    //Endpoint para mostrar la posición actual de un camion dado su id (Para mostrar en el mapa)
    @GetMapping("camion/{idCamion}/recorrido")
    public ResponseEntity<List<PosicionDTO>> obtenerRecorrido(@PathVariable Long idCamion) {
        List<PosicionDTO> recorrido = monitoreoService.obtenerRecorrido(idCamion);
        if (recorrido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recorrido);
    }


}
