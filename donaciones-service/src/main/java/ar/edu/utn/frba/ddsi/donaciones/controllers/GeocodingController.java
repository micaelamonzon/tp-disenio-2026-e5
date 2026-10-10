package ar.edu.utn.frba.ddsi.donaciones.controllers;

import ar.edu.utn.frba.ddsi.donaciones.dto.CoordenadasDTO;
import ar.edu.utn.frba.ddsi.donaciones.services.UsigGeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeocodingController {
    private final UsigGeocodingService service;

    public GeocodingController(UsigGeocodingService service) {
        this.service = service;
    }

    @GetMapping("/geocodificar")
    public ResponseEntity<CoordenadasDTO> geocodificar(@RequestParam String direccion) {
        return service.geocodificar(direccion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
