package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaJuridicaDTO;

import java.util.List;

public interface DonacionesService {
    List<PersonaDonanteDTO> obtenerTodosHumanos();
    List<PersonaDonanteDTO> obtenerTodosJuridicos();
    PersonaDonanteDTO obtenerDonacionesDeHumano(Long id);
    PersonaDonanteDTO obtenerDonacionesDeJurico(Long id);
    PersonaHumanaDTO crearDonanteHumano(PersonaHumanaDTO body);
    DonacionSinSegmentarDTO crearDonacionDeJuridico(DonacionSinSegmentarDTO body, Long id);
    DonacionSinSegmentarDTO crearDonacionDeHumano(DonacionSinSegmentarDTO body, Long id);
    List<DonacionSinSegmentarDTO> modificarDonacionDeHumano(DonacionSinSegmentarDTO body, Long idHumano, Long idDonacion, Long idBien);
    public List<DonacionSinSegmentarDTO> modificarDonacionDeJuridica(DonacionSinSegmentarDTO body, Long idJuridica, Long idDonacion, Long idBien);
    List<DonacionSinSegmentarDTO> eliminarDonacionDeHumano(Long idHumano, Long idDonacion);
    List<DonacionSinSegmentarDTO> eliminarDonacionDeJuridico(Long idJuridico, Long idDonacion);
    List<PersonaDonanteDTO> obtenerTodosLosDonantesUnificados();
}
