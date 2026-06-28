package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EstadoDonacionServiceimpl {

    private final Map<Long, DonacionSegmentada> donaciones = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Long registrarDonacion(DonacionSegmentada donacion) {
        Long id = contador.getAndIncrement();
        donacion.setId(id);
        donaciones.put(id, donacion);
        return id;
    }

    public DonacionSegmentadaDTO obtenerHistorial(Long id) {
        return toDTO(buscar(id));
    }

    public DonacionSegmentadaDTO asignar(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA,
                "Asignada a entidad beneficiaria", responsableId);
        return toDTO(d);
    }

    public DonacionSegmentadaDTO listaParaEntregar(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.LISTA_PARA_ENTREGAR,
                "Ruta planificada", responsableId);
        return toDTO(d);
    }

    public DonacionSegmentadaDTO iniciarTraslado(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.EN_TRASLADO,
                "Camión inició recorrido", responsableId);
        return toDTO(d);
    }

    public DonacionSegmentadaDTO entregar(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ENTREGADA,
                "Entidad beneficiaria confirmó recepción", responsableId);
        return toDTO(d);
    }

    public DonacionSegmentadaDTO fallarEntrega(Long id, String justificacion, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ENTREGA_FALLIDA, justificacion, responsableId);
        d.cambiarEstado(EstadoDonacion.EN_DEPOSITO,
                "Retorno automático al depósito", "SISTEMA");
        return toDTO(d);
    }

    public DonacionSegmentadaDTO vencer(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.VENCIDA,
                "Marcada como vencida", responsableId);
        return toDTO(d);
    }

    public List<DonacionSegmentada> findByEstado(String nombreEstado) {
        return donaciones.values().stream()
                .filter(d -> d.getNombreEstadoActual().equals(nombreEstado))
                .toList();
    }

    private DonacionSegmentada buscar(Long id) {
        DonacionSegmentada d = donaciones.get(id);
        if (d == null) throw new RuntimeException("Donación no encontrada: " + id);
        return d;
    }

    private DonacionSegmentadaDTO toDTO(DonacionSegmentada d) {
        DonacionSegmentadaDTO dto = new DonacionSegmentadaDTO();
        dto.setId(d.getId());
        dto.setEstadoActual(d.getNombreEstadoActual());
        dto.setHistorial(d.getHistorial().stream().map(c -> {
            CambioEstadoDTO ce = new CambioEstadoDTO();
            ce.setEstadoAnterior(c.getEstadoAnterior());
            ce.setEstadoNuevo(c.getEstadoNuevo());
            ce.setFecha(c.getFecha());
            ce.setJustificacion(c.getJustificacion());
            ce.setResponsableId(c.getResponsableId());
            return ce;
        }).toList());
        return dto;
    }
}