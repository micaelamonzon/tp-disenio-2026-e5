package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaJuridicaDTO;

import java.util.List;

public interface DonacionesService {
    List<PersonaHumanaDTO> obtenerTodosHumanos();
    List<PersonaJuridicaDTO> obtenerTodosJuridicos();
    PersonaDonanteDTO obtenerDonacionesDeHumano(Long id);
    PersonaDonanteDTO obtenerDonacionesDeJurico(Long id);
    PersonaHumanaDTO crearDonanteHumanos(PersonaHumanaDTO body);
}
