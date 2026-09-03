package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.CamionRequestDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;

import java.util.List;

public interface CamionService {

    List<Camion> obtenerCamionesDisponibles();

    CamionResponseDTO crearCamion(CamionRequestDTO request);

    List<CamionResponseDTO> listarCamiones(Boolean disponible);

    CamionResponseDTO obtenerCamionPorId(String id);

    CamionResponseDTO actualizarCamion(String id, CamionRequestDTO request);

    void eliminarCamion(String id);
}
