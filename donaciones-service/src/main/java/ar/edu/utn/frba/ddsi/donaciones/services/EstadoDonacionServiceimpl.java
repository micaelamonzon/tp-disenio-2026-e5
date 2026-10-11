package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.BienDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.CambioEstadoDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.CategoriaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.SubcategoriaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.algoritmos.Estado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonacionesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EstadoDonacionServiceimpl implements EstadoDonacionService{

    private final DonacionesRepository donacionesRepository;

    public EstadoDonacionServiceimpl(DonacionesRepository donacionesRepository) {
        this.donacionesRepository = donacionesRepository;
    }

    @Override
    public Long registrarDonacion(DonacionSegmentada donacion) {
        this.donacionesRepository.save(donacion);
        return donacion.getId();
    }

    @Override
    public DonacionSegmentadaDTO obtenerHistorial(Long id) {
        return toDTO(buscar(id));
    }

    @Override
    public DonacionSegmentadaDTO asignar(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ASIGNACION_REALIZADA,
                "Asignada a entidad beneficiaria", responsableId);
        return toDTO(d);
    }

    @Override
    public DonacionSegmentadaDTO listaParaEntregar(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.LISTA_PARA_ENTREGAR,
                "Ruta planificada", responsableId);
        this.donacionesRepository.save(d);
        return toDTO(d);
    }

    @Override
    public DonacionSegmentadaDTO iniciarTraslado(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.EN_TRASLADO,
                "Camión inició recorrido", responsableId);
        return toDTO(d);
    }

    @Override
    public DonacionSegmentadaDTO entregar(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ENTREGADA,
                "Entidad beneficiaria confirmó recepción", responsableId);
        return toDTO(d);
    }

    @Override
    public DonacionSegmentadaDTO fallarEntrega(Long id, String justificacion, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ENTREGA_FALLIDA, justificacion, responsableId);
        d.cambiarEstado(EstadoDonacion.EN_DEPOSITO,
                "Retorno automático al depósito", "SISTEMA");
        return toDTO(d);
    }

    @Override
    public DonacionSegmentadaDTO vencer(Long id, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.VENCIDA,
                "Marcada como vencida", responsableId);
        return toDTO(d);
    }

    //SE USA PARA LOGISTICAAAA
    public List<DonacionDTO> findByEstado(String nombreEstado) {
        List<DonacionDTO> donacionesDTO = new ArrayList<>();
        List<DonacionSegmentada> donacionSegmentadas = this.donacionesRepository.findByEstado(EstadoDonacion.valueOf(nombreEstado));
        donacionSegmentadas.forEach(d -> donacionesDTO.add(
                new DonacionDTO(
                        d.getId().intValue(),
                        d.getEstadoActual().toString(),
                        d.calcularPeso(),
                        d.calcularVolumen(),
                        d.getEntidadBeneficiaria().getId(),
                        d.getDonanteHumano()!= null ? d.getDonanteHumano().getId() : d.getDonanteJuridico().getId(),
                        d.getEntidadBeneficiaria().getDireccion(),
                        d.getEntidadBeneficiaria().getLatitud(),
                        d.getEntidadBeneficiaria().getLongitud(),
                        d.getSubcategoria().getNombre()
                        )));
        return donacionesDTO;
    }

    private DonacionSegmentada buscar(Long id) {
        DonacionSegmentada d = this.donacionesRepository.findById(id).orElse(null);
        if (d == null) throw new RuntimeException("Donación no encontrada: " + id);
        return d;
    }

    private DonacionSegmentada buscarPorEstado(EstadoDonacion estado) {
        DonacionSegmentada d = this.donacionesRepository.findByEstado(estado).getFirst();
        if (d == null) throw new RuntimeException("Donación no encontrada: " + estado);
        return d;
    }

    private DonacionSegmentadaDTO toDTO(DonacionSegmentada d) {
        List<CambioEstadoDTO> historialDTO = d.getHistorial().stream()
                .map(c -> new CambioEstadoDTO(
                        c.getEstadoAnterior().toString(),
                        c.getEstadoNuevo().toString(),
                        c.getFecha(),
                        c.getJustificacion(),
                        c.getResponsableId()
                ))
                .toList();

        return new DonacionSegmentadaDTO(
                d.getId(),
                d.getEstadoActual().toString(),
                historialDTO,
                this.obtenerBienesDTO(d.getBienesDelMismoTipo()),
                d.getFechaDeIngreso(),
                d.getDonacionEntregada(),
                d.getEntidadBeneficiaria().getId(),
                d.getFotoPath()
        );
    }

    public List<BienDTO> obtenerBienesDTO(List<Bien> bienes){
        List<BienDTO> bienesDTO = bienes.stream()
                .map(b -> new BienDTO(
                        b.getNombre(),
                        b.getDescripcion(),
                        new SubcategoriaDTO(
                                b.getSubcategoria().getNombre(),
                                b.getSubcategoria().getEsPerecedero(),
                                new CategoriaDTO(
                                        b.getSubcategoria()
                                                .getCategoria()
                                                .getNombre()
                                )
                        ),
                        b.getFechaDeVencimiento(),
                        b.getEsUsado(),
                        b.getTipoUnidad(),
                        b.getCantidad()
                ))
                .toList();

        return bienesDTO;
    }
}