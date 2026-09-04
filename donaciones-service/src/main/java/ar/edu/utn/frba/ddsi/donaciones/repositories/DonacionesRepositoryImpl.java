package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.utils.GeneradorIdSecuencial;
import lombok.Data;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class DonacionesRepositoryImpl implements DonacionesRepository{

    private final List<DonacionSegmentada> donaciones = new ArrayList<>();

    private final AtomicLong contador = new AtomicLong(1);
    private final GeneradorIdSecuencial generadorId;


    public DonacionesRepositoryImpl() {
        this.generadorId = new GeneradorIdSecuencial();
    }


    @Override
    public DonacionSegmentada guardarDonacion(DonacionSegmentada donacion) {
        Long id = contador.getAndIncrement();
        System.out.println("ID DE DONACION: " + id);
        donacion.setId(id);
        donaciones.add(donacion);
        return donacion;
    }

    @Override
    public List<DonacionSegmentada> findDonacionesPorIdyTipo(Long idDeDonante, TipoDonante tipoDonante){
        return donaciones.stream().filter(d -> d.getTipoDonante() == tipoDonante && d.getIdDelDonante() == idDeDonante).collect(Collectors.toList());
    }

    @Override
    public List<DonacionSegmentada> findByEstado(String nombreEstado) {
        return donaciones.stream()
                .filter(d -> d.getEstadoActual().toString().equals(nombreEstado))
                .toList();
    }

}
