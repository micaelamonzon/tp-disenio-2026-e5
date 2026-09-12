package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;


public interface DonacionesRepository extends JpaRepository<DonacionSegmentada, Long> {
    @Query("SELECT ds FROM DonacionSegmentada  ds WHERE ds.donanteHumano = :idDeHumano")
    List<DonacionSegmentada> findDonacionesPorIdHumano(Long idDeHumano);
    @Query("SELECT ds FROM DonacionSegmentada  ds WHERE ds.donanteJuridico = :idDeJuridico")
    List<DonacionSegmentada> findDonacionesPorIdJuridico(Long idDeJuridico);
    @Query("SELECT ds FROM DonacionSegmentada  ds WHERE ds.estadoActual = :estado")
    List<DonacionSegmentada> findByEstado(EstadoDonacion estado);
}
