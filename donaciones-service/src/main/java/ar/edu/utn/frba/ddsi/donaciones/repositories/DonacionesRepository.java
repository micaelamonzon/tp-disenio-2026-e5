package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;


public interface DonacionesRepository {

    List<DonacionSegmentada> findByEstado(String nombreEstado);
    DonacionSegmentada guardarDonacion(DonacionSegmentada donacion);
    List<DonacionSegmentada> findDonacionesPorIdyTipo(Long idDeDonante, TipoDonante tipoDonante);
}
