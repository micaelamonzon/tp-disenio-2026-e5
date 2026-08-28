package ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;

import java.util.ArrayList;

public class ResultadoImportacion {
    public final ArrayList<PersonaHumana> humanos;
    public final ArrayList<PersonaJuridica> juridicos;

    public ResultadoImportacion(ArrayList<PersonaHumana> humanos, ArrayList<PersonaJuridica> juridicos) {
        this.humanos = humanos;
        this.juridicos = juridicos;
    }
}
