package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Data
@Repository
public class DonantesRepositoryImpl implements DonantesRepository {

    private final List<PersonaHumana> humanos = new ArrayList<>();
    private final List<PersonaJuridica> juridicos = new ArrayList<>();


    @Override
    public List<PersonaHumana> findAllHumanos(){
        return new ArrayList<>(humanos);
    }
    @Override
    public List<PersonaJuridica> findAllJuridicos(){
        return new ArrayList<>(juridicos);
    }

    @Override
    public PersonaHumana humanoFindById(Long id){
        PersonaHumana p = humanos.stream().filter(ph -> ph.getId().equals(id)).findFirst().orElse(null);;
        return p;
    }
    @Override
    public PersonaJuridica juridicaFindById(Long id){
        PersonaJuridica p = juridicos.stream().filter(pj -> pj.getId().equals(id)).findFirst().orElse(null);;
        return p;
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
        Long id = Long.valueOf(juridicos.size() + 1);
        donante.setId(id);
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
        Long id = Long.valueOf(humanos.size() + 1);
        donante.setId(id);
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
