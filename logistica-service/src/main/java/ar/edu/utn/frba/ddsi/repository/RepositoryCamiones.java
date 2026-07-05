package ar.edu.utn.frba.ddsi.repository;

import ar.edu.utn.frba.ddsi.models.entities.Camion;

import java.util.ArrayList;
import java.util.List;

public interface RepositoryCamiones {

    public void save(Camion camion);
    public Camion findById(Long id);
    public List<Camion> findAll();
    public void delete(Long id);

}
