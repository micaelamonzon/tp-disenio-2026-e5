package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.dto.DonacionIncentivosDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.Perfil;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;
import ar.edu.utn.frba.ddsi.repositories.CategoriaDeBien;
import ar.edu.utn.frba.ddsi.repositories.CategoriaDeMisionRepository;
import ar.edu.utn.frba.ddsi.repositories.DonacionRepository;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceptorWebHookService {

    private final IncentivosRepository incentivosRepository;
    private final CategoriaDeBien categoriaRepository;
    private final CategoriaDeMisionRepository categoriaDeMisionRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReceptorWebHookService.class);
    private final IncentivosService incentivosService;
    private final DonacionRepository donacionRepository;

    public ReceptorWebHookService(IncentivosRepository incentivosRepository, CategoriaDeBien categoriaRepository, CategoriaDeMisionRepository categoriaDeMisionRepository, IncentivosService incentivosService, DonacionRepository donacionRepository) {
        this.incentivosRepository = incentivosRepository;
        this.categoriaRepository = categoriaRepository;
        this.categoriaDeMisionRepository = categoriaDeMisionRepository;
        this.incentivosService = incentivosService;
        this.donacionRepository = donacionRepository;
    }


    @Transactional
    public void registrarDonante(PersonaDonanteDTO nuevoDonanteDTO){

        if(nuevoDonanteDTO.idHumano()!= null && incentivosRepository.findByIdEnDonaciones(nuevoDonanteDTO.idHumano(), TipoDeDonante.HUMANO) != null){
            logger.warn("Intento de registrar un donante humano ya existente: {}", nuevoDonanteDTO.idHumano());

        } else if(nuevoDonanteDTO.idJuridico() != null && incentivosRepository.findByIdEnDonaciones(nuevoDonanteDTO.idJuridico(), TipoDeDonante.JURIDICO) != null){
            logger.warn("Intento de registrar un donante juridico ya existente: {}", nuevoDonanteDTO.idJuridico());

        } else if (nuevoDonanteDTO.idHumano() != null && incentivosRepository.findByIdEnDonaciones(nuevoDonanteDTO.idHumano(), TipoDeDonante.HUMANO) == null) {

            registrarDonanteDTO(nuevoDonanteDTO, TipoDeDonante.HUMANO);

        } else if (nuevoDonanteDTO.idJuridico() != null && incentivosRepository.findByIdEnDonaciones(nuevoDonanteDTO.idJuridico(), TipoDeDonante.JURIDICO) == null) {

            registrarDonanteDTO(nuevoDonanteDTO, TipoDeDonante.JURIDICO);

        } else {
            logger.warn("Intento de registrar un donante sin tipo de donante válido.");
        }
    }

    private void registrarDonanteDTO(PersonaDonanteDTO dto, TipoDeDonante tipoDeDonante) {
        List<DonacionSegmentada> donaciones = this.convertirDonacionesDTO(dto.donaciones());

        Donante nuevoDonante = tipoDeDonante == TipoDeDonante.HUMANO
                ? crearDonanteHumano(dto, donaciones)
                : crearDonanteJuridico(dto, donaciones);

        donaciones.forEach(d -> d.setDonante(nuevoDonante));

        nuevoDonante.setPerfil(new Perfil(obtenerNombreDeUsuario(dto, tipoDeDonante), null));

        nuevoDonante.getPerfil().setCategoria(null);//empieza sin categoria en el perfil

        nuevoDonante.setNombreDeUsuario(obtenerNombreDeUsuario(dto, tipoDeDonante));

        nuevoDonante.setFechaDeRegistro(dto.fechaDeRegistro());

        this.incentivosRepository.save(nuevoDonante);
    }

    private Donante crearDonanteHumano(PersonaDonanteDTO dto, List<DonacionSegmentada> donaciones) {
        return new Donante(
                dto.idHumano(),
                dto.nombre(),
                dto.apellido(),
                null,
                dto.fechaDeRegistro(),
                donaciones,
                TipoDeDonante.HUMANO);
    }

    private Donante crearDonanteJuridico(PersonaDonanteDTO dto, List<DonacionSegmentada> donaciones) {
        return new Donante(
                dto.idJuridico(),
                null,
                null,
                dto.razonSocial(),
                dto.fechaDeRegistro(),
                donaciones,
                TipoDeDonante.JURIDICO);
    }

    private String obtenerNombreDeUsuario(PersonaDonanteDTO dto, TipoDeDonante tipoDeDonante) {
        return tipoDeDonante == TipoDeDonante.HUMANO
                ? dto.nombre() + " " + dto.apellido()
                : dto.razonSocial();
    }

    public void registrarDonantes(List<PersonaDonanteDTO> nuevosDonantes){
        nuevosDonantes.forEach(dto -> registrarDonante(dto));
    }

    @Transactional
    public void registrarDonacion(DonacionIncentivosDTO nuevaDonacionDTO,TipoDeDonante tipo, Long id){
        Donante donante = this.incentivosRepository.findByIdEnDonaciones(id, tipo);

        if(donante != null){
            DonacionSegmentada donacion = new DonacionSegmentada(
                    nuevaDonacionDTO.idEnDonaciones(),
                    nuevaDonacionDTO.fechaDeIngreso(),
                    nuevaDonacionDTO.donacionEntregada(),
                    nuevaDonacionDTO.entidadBeneficiariaId(),
                    nuevaDonacionDTO.cantidadDeBienes(),
                    this.obtenerOCrearCategoria(nuevaDonacionDTO.categoria())
            );
            donacion.setDonante(donante);
            donante.agregarDonacion(donacion);

            this.incentivosRepository.save(donante);
        } else {
            logger.warn("Intento de registrar una donación para un donante inexistente: {}", id);
        }
    }
    @Transactional
    public void registrarDonaciones(List<DonacionIncentivosDTO> nuevasDonaciones, TipoDeDonante tipo, Long id){
        nuevasDonaciones.forEach(donacion -> registrarDonacion(donacion, tipo, id));
        try {
            this.incentivosService.obtenerMisionesCompletadas(id, tipo);
        } catch (Exception e) {
            logger.error("Error al verificar misiones para el donante {}: {}", id, e.getMessage());
        }
    }

    public List<DonacionSegmentada> convertirDonacionesDTO(List<DonacionIncentivosDTO> donacionesDTO) {
        if (donacionesDTO == null) return new ArrayList<>();
        return donacionesDTO.stream()
                .map(d -> new DonacionSegmentada(
                        d.idEnDonaciones(),
                        d.fechaDeIngreso(),
                        d.donacionEntregada(),
                        d.entidadBeneficiariaId(),
                        d.cantidadDeBienes(),
                        this.obtenerOCrearCategoria(d.categoria())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Categoria obtenerOCrearCategoria(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> {
                    Categoria nueva = new Categoria(nombre);
                    nueva.setNombreDeUsuario("ADMIN");
                    return categoriaRepository.save(nueva);
                });
    }

    public void  registrarDonacionEntregada(Long idDonacionEnDonaciones){
        DonacionSegmentada donacion = this.donacionRepository.findByIdEnDonaciones(idDonacionEnDonaciones);

        if(donacion != null){
            donacion.setDonacionEntregada(true);
            donacionRepository.save(donacion);
            return;
        }
    }
}
