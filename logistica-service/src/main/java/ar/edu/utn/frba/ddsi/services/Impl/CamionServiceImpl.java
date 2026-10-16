package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.CamionDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.services.CamionService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frba.ddsi.repository.impl.RepositoryCamiones;



@Service
public class CamionServiceImpl implements CamionService {

    private final RepositoryCamiones repositoryCamiones;

    public CamionServiceImpl(RepositoryCamiones repositoryCamiones) {
        this.repositoryCamiones = repositoryCamiones;
    }

    @Override
    public List<CamionDTO> obtenerCamionesDisponibles() {
        List<Camion> camiones = repositoryCamiones.findAll().stream()
                .filter(camion -> Boolean.TRUE.equals(camion.getDisponible()))
                .toList();

        List<CamionDTO> camionDTOS = new ArrayList<>();

        camiones.forEach(c -> {camionDTOS.add(
                new CamionDTO(c.getId(),c.getPatente(),c.getVolumen(), c.getAltura(),c.getCapacidadCarga(), c.getDisponible())
        );});

        return camionDTOS;
    }

    @Override
    public CamionResponseDTO crearCamion(CamionDTO request) {
        boolean patenteExistente = repositoryCamiones.findAll().stream()
                .anyMatch(c -> c.getPatente().equals(request.patente()));

        if (patenteExistente) {
            throw new IllegalStateException("Ya existe un camión con esa patente: " + request.patente());
        }

        Boolean disponible = request.disponible() != null ? request.disponible() : Boolean.TRUE;
        Camion camion = new Camion(
                request.patente(),
                request.volumen(),
                request.altura(),
                request.capacidadCarga(),
                disponible
        );
        repositoryCamiones.save(camion); // ← MongoDB genera el id solo
        return toDTO(camion);
    }

    @Override
    public List<CamionResponseDTO> listarCamiones(Boolean disponible) {
        return repositoryCamiones.findAll().stream()
                .filter(c -> disponible == null || disponible.equals(c.getDisponible()))
                .map(this::toDTO)
                .toList();
    }

    @Override
    public CamionResponseDTO obtenerCamionPorId(String id) {
        return toDTO(buscar(id));
    }

    @Override
    public CamionResponseDTO actualizarCamion(String id, CamionDTO request) {
        Camion camion = buscar(id);
        camion.setPatente(request.patente());
        camion.setVolumen(request.volumen());
        camion.setAltura(request.altura());
        camion.setCapacidadCarga(request.capacidadCarga());
        camion.setDisponible(request.disponible());
        repositoryCamiones.save(camion); // ← persiste cambios
        return toDTO(camion);
    }

    @Override
    public void eliminarCamion(String id) {
        Camion camion = buscar(id);
        if (camion.getRutaId() != null) {
            throw new IllegalStateException("No se puede eliminar un camión con ruta asignada");
        }
        repositoryCamiones.deleteById(id);
    }


    private LocalTime parseHora(String iso) {
        if (iso == null) return null;
        try {
            return OffsetDateTime.parse(iso).toLocalTime();
        } catch (Exception e) {
            return null; // formato inesperado, ajustar según el planificador
        }
    }

    private Camion buscar(String id) {
        return repositoryCamiones.findById(id)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado: " + id));
    }

    private CamionResponseDTO toDTO(Camion camion) {
        return new CamionResponseDTO(
                camion.getId(),
                camion.getPatente(),
                camion.getVolumen(),
                camion.getAltura(),
                camion.getCapacidadCarga(),
                camion.getDisponible(),
                camion.getRutaId() // ← ya es String directo
        );
    }
}