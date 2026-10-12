package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.dto.RutaEntregaDTO;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;

import java.util.List;

public interface MonitoreoService {


    PosicionDTO obtenerPosicionActualDeCamion(String id, PosicionDTO posicionDTO);

    List<PosicionDTO> obtenerRecorrido(String idCamion);

    public List<RutaEntregaDTO> obtenerRecorridoDeDonacionesDeDonante(Long idDonante);

    List<RutaEntregaDTO> obtenerRecorridoDeEntregasDeEntidad(Long idEntidad);
}
