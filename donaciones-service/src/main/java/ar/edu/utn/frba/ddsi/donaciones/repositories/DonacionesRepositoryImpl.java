package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DonacionesRepositoryImpl implements DonacionesRepository{
    @Override
    public List<Donacion> findByEstado(Estado estado){
        return null;
    }
}
