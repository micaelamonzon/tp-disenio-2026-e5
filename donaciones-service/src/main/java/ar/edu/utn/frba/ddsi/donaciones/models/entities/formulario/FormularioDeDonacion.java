package ar.edu.utn.frba.ddsi.donaciones.models.entities.formulario;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import lombok.Data;

@Data
public class FormularioDeDonacion {
    private PersonaJuridica personaJuridica;
    private PersonaHumana personaHumana;
    private String descripcionGeneral;
    private DonacionSinSegmentar donacion;

    public FormularioDeDonacion(PersonaJuridica donanteJuridico, PersonaHumana donanteHumano,String descripcionGeneral, DonacionSinSegmentar donacion) {

        this.personaJuridica = donanteJuridico;
        this.personaHumana = donanteHumano;
        this.descripcionGeneral = descripcionGeneral;
        this.donacion = donacion;
    }


}
