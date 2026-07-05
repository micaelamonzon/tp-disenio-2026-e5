package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;

import java.util.List;

public interface MonitoreoService {


    PosicionDTO obtenerPosicionActualDeCamion(Long id, PosicionDTO posicionDTO);

    List<PosicionDTO> obtenerRecorrido(Long idCamion);
}
