package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.Camion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CamionService {

    public List<Camion> obtenerCamionesDisponibles(){
        List<Camion> camiones = new ArrayList<>();

        return camiones.stream().filter(camion -> camion.getDisponible() == true).toList();
    }
}
