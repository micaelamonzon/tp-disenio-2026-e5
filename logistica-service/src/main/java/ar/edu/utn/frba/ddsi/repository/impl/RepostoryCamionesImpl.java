package ar.edu.utn.frba.ddsi.repository.impl;

import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.repository.RepositoryCamiones;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RepostoryCamionesImpl implements RepositoryCamiones {
    private final List<Camion> camiones = new ArrayList<>();

    @Override
    public void save(Camion camion){
        Long id = (long)camiones.size();
        camion.setId(id);
        camiones.add(camion);
    }

    @Override
    public Camion findById(Long id) {
        Camion camion = camiones.stream().filter(
                c -> c.getId().equals(id)).findFirst().orElse(null);;
        return camion;
    }
    @Override
    public void delete(Long id ){

        Camion camion = camiones.stream().filter(
                c -> c.getId().equals(id)).findFirst().orElse(null);;

                if(camion != null){
                    camiones.remove(camion);
                }

    }

}
