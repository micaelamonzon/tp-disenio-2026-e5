package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.utils.GeneradorIdSecuencial;
import lombok.Data;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Donacion;
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
    private final List<PersonaHumana> humanos = new ArrayList<>();
    private final List<PersonaJuridica> juridicos = new ArrayList<>();

    public DonacionesRepositoryImpl(GeneradorIdSecuencial generadorId) {
        this.generadorId = generadorId;
    }

    @Override
    public List<PersonaHumana> findAllHumanos(){
        return new ArrayList<>(humanos);
    }
    @Override
    public List<PersonaJuridica> findAllJuridicos(){
        return new ArrayList<>(juridicos); }

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

    @Override
    public PersonaJuridica saveJuridica(PersonaJuridica donante){
        if (donante.getId() != null) {
            int index = juridicos.indexOf(donante);
            if (index != -1) {
                this.juridicos.set(index, donante);

                return donante;
            }
        }
        donante.setId(generadorId.siguiente());
        this.juridicos.add(donante);

        return donante;
    }
    @Override
    public PersonaHumana saveHumana(PersonaHumana donante) {
        if (donante.getId() != null) {
            int index = humanos.indexOf(donante);
            if (index != -1) {
                this.humanos.set(index, donante);

                return donante;
            }
        }
        donante.setId(generadorId.siguiente());
        this.humanos.add(donante);

        return donante;
    }

    @Override
    public void deleteJuridica(PersonaJuridica donante){
        juridicos.removeIf(p -> p.getId() == donante.getId());
    }
    @Override
    public void deleteHumana(PersonaHumana donante){
        humanos.removeIf(p -> p.getId() == donante.getId());
    }

}
