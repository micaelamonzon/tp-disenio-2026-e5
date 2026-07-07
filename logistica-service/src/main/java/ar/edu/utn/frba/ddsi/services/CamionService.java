package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.CamionRequestDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;

import java.util.List;

public interface CamionService {

    List<Camion> obtenerCamionesDisponibles();

    CamionResponseDTO crearCamion(CamionRequestDTO request);

    List<CamionResponseDTO> listarCamiones(Boolean disponible);

    CamionResponseDTO obtenerCamionPorId(Long id);

    CamionResponseDTO actualizarCamion(Long id, CamionRequestDTO request);

    void eliminarCamion(Long id);
}
