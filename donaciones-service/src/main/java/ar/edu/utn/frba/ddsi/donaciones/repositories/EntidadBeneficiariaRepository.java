package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.EntidadBeneficiaria;

import java.util.List;

public interface EntidadBeneficiariaRepository {
    List<EntidadBeneficiaria> findAll();
    EntidadBeneficiaria findById(Long id);
    EntidadBeneficiaria save(EntidadBeneficiaria entidad);
    void delete(EntidadBeneficiaria entidad);
}
