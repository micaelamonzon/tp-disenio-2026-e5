package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.MedioDeNotificacionDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.RepresentanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Representante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.EntidadBeneficiaria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.TipoDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.repositories.EntidadBeneficiariaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntidadBeneficiariaServiceImpl implements EntidadBeneficiariaService{
    private final EntidadBeneficiariaRepository entidadBeneficiariaRepository;
    private List<MedioDeNotificacionDTO> mapearMedios(List<MedioDeNotificacion> medios) {
        return medios.stream()
                .map(m -> new MedioDeNotificacionDTO(m.getTipoDeNotificacion(), m.getContacto()))
                .toList();
    }

    // Lista modificable (no .toList()): Hibernate la administra por el cascade/orphanRemoval
    private List<MedioDeNotificacion> mapearMediosAEntidad(List<MedioDeNotificacionDTO> medios) {
        if (medios == null) return new ArrayList<>();
        return medios.stream()
                .map(dto -> new MedioDeNotificacion(dto.tipoDeNotificacion(), dto.datoDeContacto()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public EntidadBeneficiariaServiceImpl(EntidadBeneficiariaRepository entidadBeneficiariaRepository) {
        this.entidadBeneficiariaRepository = entidadBeneficiariaRepository;
    }
    @Override
    public List<EntidadBeneficiariaDTO> obtenerTodas() {
        List<EntidadBeneficiaria> entidades =
                this.entidadBeneficiariaRepository.findAll();

        return entidades.stream().map(entidad -> {
            List<RepresentanteDTO> representantesDTO = entidad.getRepresentantes()
                    .stream()
                    .map(r -> new RepresentanteDTO(
                            r.getNombre(),
                            r.getApellido(),
                            r.getDNI(),
                            mapearMedios(r.getMediosDeNotificacion())
                    ))
                    .toList();

            return new EntidadBeneficiariaDTO(
                    entidad.getId(),
                    entidad.getRazonSocial(),
                    entidad.getDireccion(),
                    entidad.getTelefono(),
                    representantesDTO
            );
        }).toList();
    }

    @Override
    public EntidadBeneficiariaDTO obtenerPorId(Long id) {
        EntidadBeneficiaria entidad = this.entidadBeneficiariaRepository.findById(id).orElseThrow();

            List<RepresentanteDTO> representantesDTO = entidad.getRepresentantes()
                    .stream()
                    .map(r -> new RepresentanteDTO(
                            r.getNombre(),
                            r.getApellido(),
                            r.getDNI(),
                            mapearMedios(r.getMediosDeNotificacion())
                    ))
                    .toList();

            return new EntidadBeneficiariaDTO(
                    entidad.getId(),
                    entidad.getRazonSocial(),
                    entidad.getDireccion(),
                    entidad.getTelefono(),
                    representantesDTO
            );
    }

    @Override
    public EntidadBeneficiariaDTO crear(EntidadBeneficiariaDTO body) {
        EntidadBeneficiaria nuevaEntidad = new EntidadBeneficiaria();
        nuevaEntidad.setRazonSocial(body.razonSocial());
        nuevaEntidad.setDireccion(body.direccion());
        nuevaEntidad.setTelefono(body.telefono());

        List<RepresentanteDTO> representantesDTO =
                body.representantes() != null ? body.representantes() : List.of();

        List<Representante> representantes = representantesDTO
                .stream()
                .map(dto -> {
                    Representante r = new Representante();
                    r.setNombre(dto.nombre());
                    r.setApellido(dto.apellido());
                    r.setDNI(dto.numeroDeDocumento());
                    r.setMediosDeNotificacion(mapearMediosAEntidad(dto.mediosDeNotificacion()));
                    return r;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        nuevaEntidad.setRepresentantes(representantes);
        EntidadBeneficiaria guardada = this.entidadBeneficiariaRepository.save(nuevaEntidad);
        return this.obtenerPorId(guardada.getId());
    }

    @Override
    public void eliminar(Long id) {
        EntidadBeneficiaria entidad =
                this.entidadBeneficiariaRepository.findById(id).orElse(null);

        if (entidad == null) {
            throw new RuntimeException("Entidad beneficiaria no encontrada");
        }

        this.entidadBeneficiariaRepository.delete(entidad);
    }
}