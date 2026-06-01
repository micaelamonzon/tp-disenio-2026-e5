package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.RepresentanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Representante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.EntidadBeneficiaria;
import ar.edu.utn.frba.ddsi.donaciones.repositories.EntidadBeneficiariaRepository;

import java.util.List;

public class EntidadBeneficiariaServiceImpl implements EntidadBeneficiariaService{
    private final EntidadBeneficiariaRepository entidadBeneficiariaRepository;

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
                            String.valueOf(r.getNumeroDeDocumento()),
                            r.getMediosDeNotificacion()
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
        EntidadBeneficiaria entidad = this.entidadBeneficiariaRepository.findById(id);

            List<RepresentanteDTO> representantesDTO = entidad.getRepresentantes()
                    .stream()
                    .map(r -> new RepresentanteDTO(
                            r.getNombre(),
                            r.getApellido(),
                            String.valueOf(r.getNumeroDeDocumento()),
                            r.getMediosDeNotificacion()
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

        List<Representante> representantes = body.representantes()
                .stream()
                .map(dto -> {
                    Representante r = new Representante();
                    r.setNombre(dto.nombre());
                    r.setApellido(dto.apellido());
                    r.setNumeroDeDocumento(Integer.parseInt(dto.numeroDeDocumento()));
                    r.setMediosDeNotificacion(dto.mediosDeNotificacion());
                    return r;
                })
                .toList();

        nuevaEntidad.setRepresentantes(representantes);
        this.entidadBeneficiariaRepository.save(nuevaEntidad);
        return this.obtenerPorId(nuevaEntidad.getId());
    }

    @Override
    public void eliminar(Long id) {
        EntidadBeneficiaria entidad =
                this.entidadBeneficiariaRepository.findById(id);

        if (entidad == null) {
            throw new RuntimeException("Entidad beneficiaria no encontrada");
        }

        this.entidadBeneficiariaRepository.delete(entidad);
    }
}