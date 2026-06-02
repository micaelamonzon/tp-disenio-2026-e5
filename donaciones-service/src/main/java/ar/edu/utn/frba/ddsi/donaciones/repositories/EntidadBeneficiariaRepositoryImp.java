package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.EntidadBeneficiaria;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Data
@Repository
public class EntidadBeneficiariaRepositoryImp implements EntidadBeneficiariaRepository{
    private final List<EntidadBeneficiaria> entidades = new ArrayList<>();

    @Override
    public List<EntidadBeneficiaria> findAll() {
        return new ArrayList<>(entidades);
    }

    @Override
    public EntidadBeneficiaria findById(Long id) {
        return entidades.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public EntidadBeneficiaria save(EntidadBeneficiaria entidad) {
        Long id = Long.valueOf(entidades.size());
        entidad.setId(id);
        this.entidades.add(entidad);
        return entidad;
    }

    @Override
    public void delete(EntidadBeneficiaria entidad) {
        entidades.removeIf(e -> e.getId().equals(entidad.getId()));
    }

}
