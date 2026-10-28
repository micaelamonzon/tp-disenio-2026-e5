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
    private final UsigGeocodingService geocodingService;

    public EntidadBeneficiariaServiceImpl(EntidadBeneficiariaRepository entidadBeneficiariaRepository, UsigGeocodingService geocodingService) {
        this.entidadBeneficiariaRepository = entidadBeneficiariaRepository;
        this.geocodingService = geocodingService;
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
                    entidad.getLatitud(),
                    entidad.getLongitud(),
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
                    entidad.getLatitud(),
                    entidad.getLongitud(),
                    representantesDTO

            );
    }

    @Override
    public EntidadBeneficiariaDTO crear(EntidadBeneficiariaDTO body) {

        EntidadBeneficiaria nuevaEntidad = new EntidadBeneficiaria();
        nuevaEntidad.setRazonSocial(body.razonSocial());
        nuevaEntidad.setDireccion(body.direccion());
        nuevaEntidad.setTelefono(body.telefono());

        geocodingService.geocodificar(nuevaEntidad.getDireccion())
                .ifPresent(c -> {
                    nuevaEntidad.setLatitud(c.latitud());
                    nuevaEntidad.setLongitud(c.longitud());
                });

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
    public EntidadBeneficiariaDTO modificar(EntidadBeneficiariaDTO body, Long id){
        if(id== null){
            throw new RuntimeException("El id no puede ser null");
        }

        EntidadBeneficiaria entidad = this.entidadBeneficiariaRepository.findById(id).orElse(null);

        if(entidad == null){
            throw new RuntimeException("No se encontró la entidad con id " + id);
        }

        if(body.razonSocial()!=null){
            entidad.setRazonSocial(body.razonSocial());
        }
        if(body.direccion()!=null){
            entidad.setDireccion(body.direccion());
        }
        if(body.telefono()!=null){
            entidad.setTelefono(body.telefono());
        }

        entidadBeneficiariaRepository.save(entidad);

        return new EntidadBeneficiariaDTO(
                entidad.getId(),
                entidad.getRazonSocial(),
                entidad.getDireccion(),
                entidad.getTelefono(),
                entidad.getLatitud(),
                entidad.getLongitud(),
                this.mapearARepresentantesDTO(entidad.getRepresentantes()));
    }


    public List<RepresentanteDTO> mapearARepresentantesDTO(List<Representante> representantes) {
        List<RepresentanteDTO> representanteDTOS = new ArrayList<>();

        representantes.forEach(r -> representanteDTOS.add(
                                                new RepresentanteDTO(
                                                r.getNombre(),
                                                r.getApellido(),
                                                r.getDNI(),
                                                this.mapearMedios(r.getMediosDeNotificacion())
                                                )
        ));

        return representanteDTOS;
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


}