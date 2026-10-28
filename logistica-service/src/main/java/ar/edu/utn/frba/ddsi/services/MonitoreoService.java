package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.dto.RutaEntregaDTO;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;
import ar.edu.utn.frba.ddsi.models.entities.TipoDeDonante;

import java.util.List;

public interface MonitoreoService {


    PosicionDTO obtenerPosicionActualDeCamion(String id, PosicionDTO posicionDTO);

    List<PosicionDTO> obtenerRecorrido(String idCamion);

    public List<RutaEntregaDTO> obtenerRecorridoDeDonacionesDeDonante(Long idDonante, TipoDeDonante tipoDeDonante);

    List<RutaEntregaDTO> obtenerRecorridoDeEntregasDeEntidad(Long idEntidad);
}
