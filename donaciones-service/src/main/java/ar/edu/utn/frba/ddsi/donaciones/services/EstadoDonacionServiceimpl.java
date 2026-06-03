package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EstadoDonacionServiceimpl implements EstadoDonacionService {

    private final Map<Long, DonacionSegmentada> donaciones = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Long registrarDonacion(DonacionSegmentada donacion) {
        Long id = contador.getAndIncrement();
        donaciones.put(id, donacion);
        return id;
    }

    public DonacionSegmentadaDTO obtenerHistorial(Long id) {
        return toDTO(id, buscar(id));
    }

    public DonacionSegmentadaDTO asignar(Long id) {
        DonacionSegmentada d = buscar(id);
        d.asignar();
        return toDTO(id, d);
    }

    public DonacionSegmentadaDTO listaParaEntregar(Long id) {
        DonacionSegmentada d = buscar(id);
        d.marcarListaParaEntregar();
        return toDTO(id, d);
    }

    public DonacionSegmentadaDTO iniciarTraslado(Long id) {
        DonacionSegmentada d = buscar(id);
        d.iniciarTraslado();
        return toDTO(id, d);
    }

    public DonacionSegmentadaDTO entregar(Long id) {
        DonacionSegmentada d = buscar(id);
        d.entregar();
        return toDTO(id, d);
    }

    public DonacionSegmentadaDTO fallarEntrega(Long id, String justificacion) {
        DonacionSegmentada d = buscar(id);
        d.fallarEntrega(justificacion);
        return toDTO(id, d);
    }

    public DonacionSegmentadaDTO vencer(Long id) {
        DonacionSegmentada d = buscar(id);
        d.marcarVencida();
        return toDTO(id, d);
    }

    private DonacionSegmentada buscar(Long id) {
        DonacionSegmentada d = donaciones.get(id);
        if (d == null) throw new RuntimeException("Donación no encontrada: " + id);
        return d;
    }
    private DonacionSegmentadaDTO toDTO(Long id, DonacionSegmentada d) {
        List<CambioEstadoDTO> historial = d.getHistorial().stream().map(c -> {
            return new CambioEstadoDTO(
                    c.getEstadoAnterior(),
                    c.getEstadoNuevo(),
                    c.getFecha(),
                    c.getJustificacion()
            );
        }).toList();

        return new DonacionSegmentadaDTO(id, d.getNombreEstadoActual(), historial);
    }
}