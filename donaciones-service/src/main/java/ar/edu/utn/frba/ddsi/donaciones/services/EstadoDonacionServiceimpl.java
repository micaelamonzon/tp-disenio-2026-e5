package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
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
    private final ServiceWebHook serviceWebHook;

    public EstadoDonacionServiceimpl(DonacionesRepository donacionesRepository, ServiceWebHook serviceWebHook) {
        this.donacionesRepository = donacionesRepository;
        this.serviceWebHook = serviceWebHook;
    }

    public DonacionSegmentadaDTO asignar(Long id, Necesidad necesidad, String responsableId) {
        DonacionSegmentada d = buscar(id);
        d.asignarA(necesidad, responsableId);
        this.donacionesRepository.save(d);   // explícito, igual que en vencer
        return toDTO(d);
    }

    public List<DonacionAdminDTO> listarParaAdmin(String estado) {
        return donacionesRepository.findByEstado(EstadoDonacion.valueOf(estado))
                .stream().map(this::toAdminDTO).toList();
    }

    private DonacionAdminDTO toAdminDTO(DonacionSegmentada d) {
        boolean esHumano = d.getDonanteHumano() != null;
        var sub = d.getSubcategoria();
        return new DonacionAdminDTO(
                d.getId(),
                d.getEstadoActual().toString(),
                new SubcategoriaDTO(sub.getNombre(), sub.getEsPerecedero(),
                        new CategoriaDTO(sub.getCategoria().getNombre())),
                obtenerBienesDTO(d.getBienesDelMismoTipo()),
                d.getFechaDeIngreso(),
                esHumano ? "HUMANO" : "JURIDICO",
                esHumano ? d.getDonanteHumano().getId() : d.getDonanteJuridico().getId(),
                esHumano ? d.getDonanteHumano().getNombre() + " " + d.getDonanteHumano().getApellido()
                        : d.getDonanteJuridico().getRazonSocial(),
                d.getEstadoActual().puedeTransicionarA(EstadoDonacion.VENCIDA)
        );
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
        this.donacionesRepository.save(d);
        return toDTO(d);
    }

    @Override
    public DonacionSegmentadaDTO entregar(Long id, String responsableId) {
        return entregar(id, responsableId, null);
    }

    // Confirma la entrega y guarda la foto de la recepción
    // fotoPath es un solo campo (lo usa la landing): guardamos la primera foto
    // La lista completa de fotos queda en la entrega en logística
    public DonacionSegmentadaDTO entregar(Long id, String responsableId, List<String> fotosUrl) {
        DonacionSegmentada d = buscar(id);
        d.cambiarEstado(EstadoDonacion.ENTREGADA,
                "Entidad beneficiaria confirmó recepción", responsableId);
        if (fotosUrl != null && !fotosUrl.isEmpty()) {
            d.setFotoPath(fotosUrl.get(0));
        }
        d.setDonacionEntregada(true); // lo usa incentivos para la misión "Donaciones Exitosas"
        this.donacionesRepository.save(d); // sin esto el cambio de estado no se persistía
        this.serviceWebHook.notificarDonacionEntregada(d);
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
        this.donacionesRepository.save(d);
        return toDTO(d);
    }

    //SE USA PARA LOGISTICAAAA
    public List<DonacionDTO> findByEstado(String nombreEstado) {
        List<DonacionDTO> donacionesDTO = new ArrayList<>();
        List<DonacionSegmentada> donacionSegmentadas = this.donacionesRepository.findByEstado(EstadoDonacion.valueOf(nombreEstado));
        donacionSegmentadas.forEach(d -> {
                var entidad = d.getEntidadBeneficiaria();
                donacionesDTO.add(
                new DonacionDTO(
                        d.getId().intValue(),
                        d.getDonanteHumano()!= null ? TipoDonante.HUMANO : TipoDonante.JURIDICO,
                        d.getEstadoActual().toString(),
                        d.calcularPeso(),
                        d.calcularVolumen(),
                        entidad != null ? entidad.getId() : null,
                        d.getDonanteHumano()!= null ? d.getDonanteHumano().getId() : d.getDonanteJuridico().getId(),
                        entidad != null ? entidad.getDireccion() : null,
                        entidad != null ? entidad.getLatitud() : null,
                        entidad != null ? entidad.getLongitud() : null,
                        d.getSubcategoria().getNombre()
                        ));
        });
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
                d.getFechaDeAsignacion(),
                d.getDonacionEntregada(),
                d.getEntidadBeneficiaria() != null ? d.getEntidadBeneficiaria().getId() : null,
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