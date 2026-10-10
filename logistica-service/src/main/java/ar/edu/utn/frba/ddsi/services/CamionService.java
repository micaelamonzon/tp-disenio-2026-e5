package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.CamionDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;

import java.util.List;

public interface CamionService {

    List<CamionDTO> obtenerCamionesDisponibles();

    CamionResponseDTO crearCamion(CamionDTO request);

    List<CamionResponseDTO> listarCamiones(Boolean disponible);

    CamionResponseDTO obtenerCamionPorId(String id);

    CamionResponseDTO actualizarCamion(String id, CamionDTO request);

    void eliminarCamion(String id);
}
