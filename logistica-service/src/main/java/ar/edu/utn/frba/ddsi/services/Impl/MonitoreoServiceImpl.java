package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.dto.RutaEntregaDTO;
import ar.edu.utn.frba.ddsi.dto.UltimoAvanceDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Donacion;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;
import ar.edu.utn.frba.ddsi.models.entities.TipoDeDonante;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryCamiones;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryDonacion;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import ar.edu.utn.frba.ddsi.services.MonitoreoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MonitoreoServiceImpl implements MonitoreoService {

    private final RepositoryCamiones repositoryCamiones;
    private final RepositoryDonacion repositoryDonacion;
    private final RepositoryEntregas repositoryEntregas;
    private final OsrmRutaService osrmRutaService;
    private final RutaCamionService rutaCamionService;
    private final EntregaServiceImpl entregaService;



    public MonitoreoServiceImpl(RepositoryCamiones repositoryCamiones,RepositoryDonacion repositoryDonacion, RepositoryEntregas repositoryEntregas, OsrmRutaService osrmRutaService, RutaCamionService rutaCamionService,  EntregaServiceImpl entregaService) {
        this.repositoryCamiones = repositoryCamiones;
        this.repositoryDonacion = repositoryDonacion;
        this.repositoryEntregas = repositoryEntregas;
        this.osrmRutaService = osrmRutaService;
        this.rutaCamionService = rutaCamionService;
        this.entregaService = entregaService;
    }

    @Override
    public PosicionDTO obtenerPosicionActualDeCamion(String id, PosicionDTO posicionDTO) {
        Camion camion = this.repositoryCamiones.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + id));

        Posicion nuevaPosicion = new Posicion(posicionDTO.latitud(), posicionDTO.longitud());
        camion.agregarPosicion(nuevaPosicion);
        repositoryCamiones.save(camion);

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
    public List<RutaEntregaDTO> obtenerRecorridoDeDonacionesDeDonante(Long idDonante, TipoDeDonante tipoDeDonante){

        List<Donacion> donaciones = this.repositoryDonacion.findByDonanteIdAndTipoDeDonante(idDonante,tipoDeDonante);
        if(donaciones.isEmpty()){
            return List.of();
        }

        List<Long> idsDeDonacionesEnDonaciones = donaciones.stream().map(Donacion::getIdEnDonaciones).toList();

        List<Entrega> entregas = this.repositoryEntregas.findByDonacionesIdsIn(idsDeDonacionesEnDonaciones).stream().filter(e -> e.getEstadoActual().equals(EstadoEntrega.EN_TRASLADO) ||  e.getEstadoActual().equals(EstadoEntrega.PENDIENTE)).toList();

        if (entregas.isEmpty()) return List.of();

        this.iniciarTrasladoSiCorresponde(entregas);

        List<RutaEntregaDTO> rutas = entregas.stream()
                .map(e -> this.armarRutaDeEntrega(e, donaciones))
                .toList();

        // Por camión: la parada más lejana entre las entregas de ESTE donante
        Map<String, Integer> limitePorCamion = rutas.stream()
                .filter(r -> r.camionId() != null && r.indiceParada() != null)
                .collect(Collectors.toMap(RutaEntregaDTO::camionId, RutaEntregaDTO::indiceParada, Math::max));

        return rutas.stream()
                .map(r -> recortarHasta(r, limitePorCamion.getOrDefault(r.camionId(), r.indiceParada())))
                .toList();
    }


    // Rutas de las entregas activas de una entidad beneficiaria (req. 14).
    // Reusa armarRutaDeEntrega (la misma que usa el donante) y recorta la ruta en el
    // punto de la entidad: el camión pasa por varias entidades, pero a esta solo le
    // interesa el tramo hasta ella (no se muestran las paradas de otras entidades).
    @Override
    public List<RutaEntregaDTO> obtenerRecorridoDeEntregasDeEntidad(Long idEntidad) {
        List<Entrega> entregas = this.repositoryEntregas.findAll().stream()
                .filter(e -> idEntidad.equals(e.getEntidadBeneficiariaId()))
                .filter(e -> e.getEstadoActual() == EstadoEntrega.PENDIENTE
                        || e.getEstadoActual() == EstadoEntrega.EN_TRASLADO)
                .filter(e -> e.getCamionId() != null) // sin camión asignado todavía no hay ruta
                .toList();

        if (entregas.isEmpty()) return List.of();

        // Donaciones de esas entregas, para mostrar sus subcategorías en el mapa
        List<Long> idsDonaciones = entregas.stream()
                .flatMap(e -> e.getDonacionesIds().stream())
                .toList();
        List<Donacion> donaciones = this.repositoryDonacion.findAll().stream()
                .filter(d -> idsDonaciones.contains(d.getIdEnDonaciones()))
                .toList();

        List<RutaEntregaDTO> rutasCompletas = new ArrayList<>();

        for (Entrega entrega : entregas) {
            try {
                rutasCompletas.add(armarRutaDeEntrega(entrega, donaciones));
            } catch (RuntimeException e) {
                System.out.println("No se pudo armar la ruta de la entrega " + entrega.getId() + ": " + e.getMessage());
            }
        }

        // Por camión: la parada más lejana entre las entregas de ESTA entidad
        Map<String, Integer> limitePorCamion = rutasCompletas.stream()
                .filter(r -> r.camionId() != null && r.indiceParada() != null)
                .collect(Collectors.toMap(RutaEntregaDTO::camionId, RutaEntregaDTO::indiceParada, Math::max));

        return rutasCompletas.stream()
                .map(r -> recortarHasta(r, limitePorCamion.getOrDefault(r.camionId(), r.indiceParada())))
                .toList();
    }

    // Deja la ruta del camión solo hasta la parada de esta entrega
    private RutaEntregaDTO recortarHastaLaParada(RutaEntregaDTO r) {
        if (r.rutaCompleta() == null || r.indiceParada() == null || r.indiceParada() < 0) return r;
        int fin = Math.min(r.indiceParada() + 1, r.rutaCompleta().size());
        return new RutaEntregaDTO(
                r.entregaId(), r.camionId(), r.estadoActual(), r.direccion(), r.destino(),
                new ArrayList<>(r.rutaCompleta().subList(0, fin)),
                r.indiceParada(), r.subcategorias(), r.ultimoAvance());
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

    private void iniciarTrasladoSiCorresponde(List<Entrega> entregas) {
        entregas.stream()
                .filter(e -> e.getEstadoActual() == EstadoEntrega.PENDIENTE)
                .forEach(e -> entregaService.iniciarTrasladoDeEntrega(e.getId(), "SISTEMA_LOGISTICA"));
    }

    private RutaEntregaDTO recortarHasta(RutaEntregaDTO r, Integer limite) {
        if (r.rutaCompleta() == null || limite == null || limite < 0) return r;

        int fin = Math.min(limite + 1, r.rutaCompleta().size());
        List<PosicionDTO> corta = new ArrayList<>(r.rutaCompleta().subList(0, fin));

        // Si el camión ya pasó el límite, lo dejamos clavado en el último punto de la ruta recortada
        UltimoAvanceDTO avance = r.ultimoAvance();
        if (avance != null && avance.indiceRuta() != null && avance.indiceRuta() > limite) {
            avance = new UltimoAvanceDTO(corta.get(fin - 1), avance.fecha(), limite);
        }

        return new RutaEntregaDTO(
                r.entregaId(), r.camionId(), r.estadoActual(), r.direccion(), r.destino(),
                corta, r.indiceParada(), r.subcategorias(), avance);
    }
}