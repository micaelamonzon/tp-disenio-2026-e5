package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.EntidadBeneficiariaDTO;

import java.util.List;

public interface EntidadBeneficiariaService {
    List<EntidadBeneficiariaDTO> obtenerTodas();
    EntidadBeneficiariaDTO obtenerPorId(Long id);
    EntidadBeneficiariaDTO crear(EntidadBeneficiariaDTO body);
    void eliminar(Long id);
}
