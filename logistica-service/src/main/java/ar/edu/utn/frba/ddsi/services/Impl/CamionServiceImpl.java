package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.CamionRequestDTO;
import ar.edu.utn.frba.ddsi.dto.CamionResponseDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.services.CamionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import ar.edu.utn.frba.ddsi.repository.RepositoryCamiones;



@Service
public class CamionServiceImpl implements CamionService {

    private final RepositoryCamiones repositoryCamiones;

    public CamionServiceImpl(RepositoryCamiones repositoryCamiones) {
        this.repositoryCamiones = repositoryCamiones;
    }

    @Override
    public List<Camion> obtenerCamionesDisponibles() {
        return repositoryCamiones.findAll().stream()
                .filter(camion -> Boolean.TRUE.equals(camion.getDisponible()))
                .toList();
    }

    @Override
    public CamionResponseDTO crearCamion(CamionRequestDTO request) {
        boolean patenteExistente = repositoryCamiones.findAll().stream()
                .anyMatch(c -> c.getPatente().equals(request.getPatente()));
        if (patenteExistente) {
            throw new IllegalStateException("Ya existe un camión con esa patente: " + request.getPatente());
        }

        Boolean disponible = request.getDisponible() != null ? request.getDisponible() : Boolean.TRUE;
        Camion camion = new Camion(
                request.getPatente(),
                request.getVolumen(),
                request.getAltura(),
                request.getCapacidadCarga(),
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
    public CamionResponseDTO actualizarCamion(String id, CamionRequestDTO request) {
        Camion camion = buscar(id);
        camion.setPatente(request.getPatente());
        camion.setVolumen(request.getVolumen());
        camion.setAltura(request.getAltura());
        camion.setCapacidadCarga(request.getCapacidadCarga());
        camion.setDisponible(request.getDisponible());
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