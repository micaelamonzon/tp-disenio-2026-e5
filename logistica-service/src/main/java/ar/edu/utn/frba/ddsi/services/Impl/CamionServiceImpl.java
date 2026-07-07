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

@Service
public class CamionServiceImpl implements CamionService {

    private final Map<Long, Camion> camiones = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Camion> obtenerCamionesDisponibles() {
        return camiones.values().stream()
                .filter(camion -> Boolean.TRUE.equals(camion.getDisponible()))
                .toList();
    }

    @Override
    public CamionResponseDTO crearCamion(CamionRequestDTO request) {
        boolean patenteExistente = camiones.values().stream()
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
        Long id = contador.getAndIncrement();
        camion.setId(id);
        camiones.put(id, camion);
        return toDTO(camion);
    }

    @Override
    public List<CamionResponseDTO> listarCamiones(Boolean disponible) {
        return camiones.values().stream()
                .filter(c -> disponible == null || disponible.equals(c.getDisponible()))
                .map(this::toDTO)
                .toList();
    }

    @Override
    public CamionResponseDTO obtenerCamionPorId(Long id) {
        return toDTO(buscar(id));
    }

    @Override
    public CamionResponseDTO actualizarCamion(Long id, CamionRequestDTO request) {
        Camion camion = buscar(id);
        camion.setPatente(request.getPatente());
        camion.setVolumen(request.getVolumen());
        camion.setAltura(request.getAltura());
        camion.setCapacidadCarga(request.getCapacidadCarga());
        camion.setDisponible(request.getDisponible());
        return toDTO(camion);
    }

    @Override
    public void eliminarCamion(Long id) {
        Camion camion = buscar(id);
        if (camion.getRuta() != null) {
            throw new IllegalStateException("No se puede eliminar un camión con ruta asignada");
        }
        camiones.remove(id);
    }

    private Camion buscar(Long id) {
        Camion c = camiones.get(id);
        if (c == null) throw new RuntimeException("Camión no encontrado: " + id);
        return c;
    }

    private CamionResponseDTO toDTO(Camion camion) {
        return new CamionResponseDTO(
                camion.getId(),
                camion.getPatente(),
                camion.getVolumen(),
                camion.getAltura(),
                camion.getCapacidadCarga(),
                camion.getDisponible(),
                camion.getRuta() != null ? camion.getRuta().id : null
        );
    }
}
