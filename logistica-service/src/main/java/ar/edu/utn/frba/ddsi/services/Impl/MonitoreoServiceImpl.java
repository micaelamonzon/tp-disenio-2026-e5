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
    public PosicionDTO obtenerPosicionActualDeCamion(Long id, PosicionDTO posicionDTO){

        Camion camion = this.repositoryCamiones.findById(id);

        Posicion nuevaPosicion = new Posicion(posicionDTO.latitud(), posicionDTO.longitud());

        camion.agregarPosicion(nuevaPosicion);


        return posicionDTO;

    }

    @Override
    public List<PosicionDTO> obtenerRecorrido(Long idCamion){
        Camion camion = this.repositoryCamiones.findById(idCamion);
        List<Posicion> posiciones = camion.getPosiciones();
        List<PosicionDTO> posicionesDTO = posiciones.stream().map(this::mapPoscionDTO).toList();

        return posicionesDTO;
    }

    public PosicionDTO mapPoscionDTO(Posicion posicion){
        PosicionDTO posicionDTO = new PosicionDTO(posicion.getLatitud(), posicion.getLongitud());

        return posicionDTO;
    }


}
