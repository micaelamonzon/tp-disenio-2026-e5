package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;
import ar.edu.utn.frba.ddsi.repository.RepositoryCamiones;
import ar.edu.utn.frba.ddsi.services.MonitoreoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitoreoServiceImpl implements MonitoreoService {

    private final RepositoryCamiones repositoryCamiones;

    public MonitoreoServiceImpl(RepositoryCamiones repositoryCamiones) {
        this.repositoryCamiones = repositoryCamiones;
    }

    @Override
    public PosicionDTO obtenerPosicionActualDeCamion(Long id, PosicionDTO posicionDTO) {
        Camion camion = this.repositoryCamiones.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + id));

        Posicion nuevaPosicion = new Posicion(posicionDTO.latitud(), posicionDTO.longitud());
        camion.agregarPosicion(nuevaPosicion);
        repositoryCamiones.save(camion); // ← persiste la nueva posición en MySQL

        return posicionDTO;
    }

    @Override
    public List<PosicionDTO> obtenerRecorrido(Long idCamion) {
        Camion camion = this.repositoryCamiones.findById(idCamion)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + idCamion));

        return camion.getPosiciones().stream()
                .map(this::mapPoscionDTO)
                .toList();
    }

    public PosicionDTO mapPoscionDTO(Posicion posicion) {
        return new PosicionDTO(posicion.getLatitud(), posicion.getLongitud());
    }
}