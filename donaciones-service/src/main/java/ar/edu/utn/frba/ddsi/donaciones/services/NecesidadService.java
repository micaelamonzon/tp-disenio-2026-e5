package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.NecesidadDTO;

import java.util.List;

public interface NecesidadService {
    List<NecesidadDTO> obtenerNecesidades(Long idEntidad);
    NecesidadDTO crearNecesidad(Long idEntidad, NecesidadDTO body);
    NecesidadDTO modificarNecesidad(Long idEntidad, Long idNecesidad, NecesidadDTO body);
    void eliminarNecesidad(Long idEntidad, Long idNecesidad);
}
