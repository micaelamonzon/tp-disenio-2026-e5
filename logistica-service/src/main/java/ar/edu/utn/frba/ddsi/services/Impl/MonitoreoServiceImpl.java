package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.dto.RutaEntregaDTO;
import ar.edu.utn.frba.ddsi.dto.UltimoAvanceDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Donacion;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;
import ar.edu.utn.frba.ddsi.repository.RepositoryCamiones;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryDonacion;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryRutas;
import ar.edu.utn.frba.ddsi.services.MonitoreoService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MonitoreoServiceImpl implements MonitoreoService {

    private final RepositoryCamiones repositoryCamiones;
    private final RepositoryDonacion repositoryDonacion;
    private final RepositoryEntregas repositoryEntregas;
    private final OsrmRutaService osrmRutaService;
    private final RutaCamionService rutaCamionService;



    public MonitoreoServiceImpl(RepositoryCamiones repositoryCamiones,RepositoryDonacion repositoryDonacion, RepositoryEntregas repositoryEntregas, OsrmRutaService osrmRutaService, RutaCamionService rutaCamionService) {
        this.repositoryCamiones = repositoryCamiones;
        this.repositoryDonacion = repositoryDonacion;
        this.repositoryEntregas = repositoryEntregas;
        this.osrmRutaService = osrmRutaService;
        this.rutaCamionService = rutaCamionService;
    }

    @Override
    public PosicionDTO obtenerPosicionActualDeCamion(String id, PosicionDTO posicionDTO) {
        Camion camion = this.repositoryCamiones.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + id));

        Posicion nuevaPosicion = new Posicion(posicionDTO.latitud(), posicionDTO.longitud());
        camion.agregarPosicion(nuevaPosicion);
        repositoryCamiones.save(camion); // ← persiste la nueva posición en MySQL

        return posicionDTO;
    }

    @Override
    public List<PosicionDTO> obtenerRecorrido(String idCamion) {
        Camion camion = this.repositoryCamiones.findById(idCamion)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + idCamion));

        return camion.getPosiciones().stream()
                .map(this::mapPoscionDTO)
                .toList();
    }

    public PosicionDTO mapPoscionDTO(Posicion posicion) {
        return new PosicionDTO(posicion.getLatitud(), posicion.getLongitud());
    }

    @Override
    public List<RutaEntregaDTO> obtenerRecorridoDeDonacionesDeDonante(Long idDonante){

        List<Donacion> donaciones = this.repositoryDonacion.findByDonanteId(idDonante);
        if(donaciones.isEmpty()){
             throw new RuntimeException("Este id " + idDonante + " no fue encontrado en ninguna donación");
        }

        List<Long> idsDeDonacionesEnDonaciones = donaciones.stream().map(Donacion::getIdEnDonaciones).toList();

        List<Entrega> entregas = this.repositoryEntregas.findByDonacionesIdsIn(idsDeDonacionesEnDonaciones);


         return entregas.stream()
                 .map( e -> this.armarRutaDeEntrega(e,donaciones))
                 .toList();
    }


    private RutaEntregaDTO armarRutaDeEntrega(Entrega entrega, List<Donacion> donacionesDelDonante) {
        double lat = Double.parseDouble(entrega.getLatitud());
        double lon = Double.parseDouble(entrega.getLongitud());

        List<String> subcategorias = donacionesDelDonante.stream()
                .filter(d -> entrega.getDonacionesIds().contains(d.getIdEnDonaciones()))
                .map(Donacion::getSubcategoria)
                .distinct()
                .toList();

        RutaCamionService.RutaCamion rutaCamion = rutaCamionService.armarRutaPara(entrega);

        return new RutaEntregaDTO(
                entrega.getId(),
                entrega.getCamionId(),
                entrega.getEstadoActual().toString(),
                entrega.getDireccion(),
                new PosicionDTO(lat, lon),
                rutaCamion.ruta(),
                rutaCamion.indiceDe(entrega.getId()),
                subcategorias,
                obtenerUltimoAvance(entrega)
        );
    }

    public String convertirEstado(EstadoEntrega estadoEntrega) {
        if(EstadoEntrega.EN_TRASLADO.equals(estadoEntrega)){
            return "EN TRASLADO";
        } else if (EstadoEntrega.ENTREGADA.equals(estadoEntrega)) {
            return "ENTREGADA";
        }else {
            return " ";
        }
    }
    private UltimoAvanceDTO obtenerUltimoAvance(Entrega entrega) {
        if (entrega.getCamionId() == null) return null;

        return repositoryCamiones.findById(entrega.getCamionId())
                .flatMap(camion -> camion.getPosiciones().stream()
                        .filter(p -> p.getFecha() != null)
                        .max(Comparator.comparing(Posicion::getFecha)))
                .map(p -> new UltimoAvanceDTO(
                        new PosicionDTO(p.getLatitud(), p.getLongitud()),
                        p.getFecha(),
                        p.getIndiceRuta()))
                .orElse(null);
    }
}