package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.BienDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.CategoriaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaJuridicaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.SubcategoriaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Categoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonacionesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonacionesServiceImpl implements DonacionesService {
    private final DonacionesRepository donacionesRepository;

    public DonacionesServiceImpl(DonacionesRepository donacionesRepository) {
        this.donacionesRepository = donacionesRepository;
    }

    @Override
   public List<PersonaHumanaDTO> obtenerTodosHumanos(){
        List<PersonaHumana> humanos = this.donacionesRepository.findAllHumanos();

        return humanos.stream().map(persona -> {

            List<DonacionSinSegmentarDTO> donacionesDTO = this.donacionesSinSegmentarDTO(persona);

            return new PersonaHumanaDTO(
                    persona.getId(),
                    persona.getNombre(),
                    persona.getApellido(),
                    persona.getNumeroDeDocumento(),
                    persona.getGenero(),
                    persona.getEdad(),
                    persona.getDireccion(),
                    donacionesDTO
            );

        }).toList();
    }


    @Override
    public List<PersonaJuridicaDTO> obtenerTodosJuridicos(){

        List<PersonaJuridica> juridicos = this.donacionesRepository.findAllJuridicos();
        //transformar a dto

        return null;
    }

    @Override
    public List <DonacionSinSegmentarDTO> obtenerDonacionesDeHumano(Long id) {
        PersonaHumana personaHumana = this.donacionesRepository.humanoFindById(id);
        if (personaHumana != null) {
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTOS = this.donacionesSinSegmentarDTO(personaHumana);
            return donacionSinSegmentarDTOS;
        }
        throw new RuntimeException("Persona no encontrada");
    }

    @Override
    public PersonaHumanaDTO crearDonanteHumanos(PersonaHumanaDTO body){

        PersonaHumana nuevaPersona = new PersonaHumana(
                body.nombre(),
                body.apellido(),
                body.edad(),
                body.DNI(),
                body.genero(),
                body.direccion()
                );

        List<DonacionSinSegmentar> donaciones = this.convertirDonacionesDTO(body.donaciones());
        nuevaPersona.setDonaciones(donaciones);

        this.donacionesRepository.save(nuevaPersona);

        List<DonacionSinSegmentarDTO> donacionesDTO = donacionesSinSegmentarDTO(nuevaPersona);
        PersonaHumanaDTO nuevaPersonaDTO = new PersonaHumanaDTO(
                nuevaPersona.getId(),
                nuevaPersona.getNombre(),
                nuevaPersona.getApellido(),
                nuevaPersona.getNumeroDeDocumento(),
                nuevaPersona.getGenero(),
                nuevaPersona.getEdad(),
                nuevaPersona.getDireccion(),
                donacionesDTO
                );

        return nuevaPersonaDTO;
    }

    public List<DonacionSinSegmentarDTO> donacionesSinSegmentarDTO(PersonaHumana personaHumana){

        List<DonacionSinSegmentarDTO> donaciones = personaHumana.getDonaciones().stream().map(
                donacion -> {

                    List<BienDTO> bienesDTO =
                            donacion.getBienes().stream()
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

                    return new DonacionSinSegmentarDTO(
                            bienesDTO,
                            donacion.getFechaDeIngreso()
                    );
                }).toList();

        return donaciones;
    }
    public List<DonacionSinSegmentar> convertirDonacionesDTO(List<DonacionSinSegmentarDTO> donacionesDTO){

        System.out.println(donacionesDTO);
        List<DonacionSinSegmentar> donaciones = donacionesDTO.stream().map(
                donacion -> {
                    List<Bien> bienes =
                            donacion.bienes().stream()
                                    .map(b -> new Bien(
                                            b.nombre(),
                                            b.descripcion(),
                                            null,
                                            new Subcategoria(
                                                    b.subcategoria().nombre(),
                                                    b.subcategoria().esPerecedero(),
                                                    new Categoria(
                                                            b.subcategoria().categoria().nombre()
                                                    )
                                            ),
                                            b.fechaDeVencimiento(),
                                            b.esUsado(),
                                            b.tipoUnidad(),
                                            b.cantidad()
                                    ))
                                    .toList();

                    return new DonacionSinSegmentar(
                            bienes,
                            donacion.fechaDeIngreso()
                    );
                }).toList();

        return donaciones;
    }
}
