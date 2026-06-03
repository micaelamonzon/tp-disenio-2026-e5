package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NecesidadRepositoryImpl implements NecesidadesRepository{
    @Override
        // Spring interpreta "False" al final y busca registros donde estaSatisfecha sea false
    public List<Necesidad> findByEstaSatisfechaFalse(){
        return null;
    }

    @Override
    public Necesidad findById(Long necesidadId){
        return null;
    }
}
